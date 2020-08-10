package com.xiaoshu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Course;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.StudentService;
import com.xiaoshu.service.UserService;
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

		List<Course> list = ss.findCourse();
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
		Integer userId = s.getId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				s.setCreatetime(new Date());
				ss.updateStudent(s);
				result.put("success", true);
			}else {   // 添加
				s.setCreatetime(new Date());
				ss.addStudent(s);
				result.put("success", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	// 部门添加
		@RequestMapping("addCourse")
		public void reserveCourse(HttpServletRequest request,Course c,HttpServletResponse response){
			
			JSONObject result=new JSONObject();
			try {
				List<Course> list = ss.findBycode(c.getCode());
				if(list.size()>0){  // 没有重复可以添加
					
					result.put("success", true);
					result.put("errorMsg", "该编号被使用");
				} else {
					c.setCreatetime(new Date());
					ss.addCourse(c);
					result.put("success", true);
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
		String[] str = {"id","学生编号","姓名","年龄","所选课程","所属年级"
				,"入校时间","创建时间"};
		HSSFRow h = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			h.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ss.findAll();
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow hr = sheet.createRow(i+1);
			hr.createCell(0).setCellValue(v.getId());
			hr.createCell(1).setCellValue(v.getCode());
			hr.createCell(2).setCellValue(v.getName());
			hr.createCell(3).setCellValue(v.getAge());
			hr.createCell(4).setCellValue(v.getCname());
			hr.createCell(5).setCellValue(v.getGrade());
			hr.createCell(6).setCellValue(TimeUtil.formatTime(v.getEntrytime(),"yyyy-MM-dd"));
			hr.createCell(7).setCellValue(TimeUtil.formatTime(v.getCreatetime(),"yyyy-MM-dd"));
		}
		File file = new File("d:/学生表.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		WriterUtil.write(response, result.toString());
	}
	
	// 柱状图
	@RequestMapping("echarts")
	public void echarts(HttpServletRequest request,HttpServletResponse response) throws Exception{
		List<Vo> list = ss.findEcharts();
		Object json = JSONObject.toJSON(list);
		WriterUtil.write(response, json.toString());
	}
}
