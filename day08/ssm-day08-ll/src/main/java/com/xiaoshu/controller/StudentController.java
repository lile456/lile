package com.xiaoshu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.School;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.StudentService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("student")
public class StudentController extends LogController{
	@Autowired
	private OperationService operationService;
	@Autowired
	private StudentService ss;
	
	
	@RequestMapping("studentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{

		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<School> list = ss.findSchool();
		request.setAttribute("list", list);
		
		return "student";
	}
	
	
	@RequestMapping(value="studentList",method=RequestMethod.POST)
	public void userList(Vo v,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Vo> userList= ss.findVoPage(v, pageNum, pageSize);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveStudent")
	public void reserveUser(HttpServletRequest request,Student s,HttpServletResponse response){
		Integer userId = s.getSid();
		JSONObject result=new JSONObject();
		List<Student> list = ss.findStudent(s.getName());
		try {
			if (userId != null) {   // userId不为空 说明是修改
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}else{
					ss.updateStudent(s);
					result.put("success", true);
				}
			}else {   // 添加
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				} else {
					ss.addStudent(s);
					result.put("success", true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteStudent")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				ss.deleteStudent(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	// 导出
	@RequestMapping("dc")
	public void dc(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject result=new JSONObject();
		
		HSSFWorkbook hw = new HSSFWorkbook();
		HSSFSheet sheet = hw.createSheet();
		String[] str = {"学生编号","学生姓名","学生生日","学生年龄"
				,"学生性别","学校名称"};
		HSSFRow h = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			h.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ss.findAll();
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(v.getSid());
			row.createCell(1).setCellValue(v.getName());
			row.createCell(2).setCellValue(TimeUtil.formatTime(v.getDate(),"yyyy-MM-dd"));
			row.createCell(3).setCellValue(v.getAge());
			row.createCell(4).setCellValue(v.getSex());
			row.createCell(5).setCellValue(v.getSname());
		}
		File file = new File("d:/学生表.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}
	// 导入
		@RequestMapping("dr")
		public void dr(MultipartFile xls,HttpServletRequest request,HttpServletResponse response) throws Exception{
			JSONObject result=new JSONObject();
			
			HSSFWorkbook hw = new HSSFWorkbook(xls.getInputStream());
			HSSFSheet sheet = hw.getSheetAt(0);
			int last = sheet.getLastRowNum();
			for (int i = 1; i <= last; i++) {
				Student s = new Student();
				HSSFRow hr = sheet.getRow(i);
				s.setName(hr.getCell(1).getStringCellValue());
				s.setDate(hr.getCell(2).getDateCellValue());
				s.setAge(hr.getCell(3).getStringCellValue());
				s.setSex(hr.getCell(4).getStringCellValue());
				String name = hr.getCell(5).getStringCellValue();
				
				School c = ss.findByName(name);
				if(c==null){
					School c1 = new School();
					c1.setName(name);
					ss.addByName(c1);
					School c2 = ss.findByName(name);
					s.setId(c2.getId());
				}else {
					s.setId(c.getId());
				}
				ss.addStudent(s);
			}
			
			hw.close();
			
			result.put("success", true);
			WriterUtil.write(response, result.toString());
		}
	
}
