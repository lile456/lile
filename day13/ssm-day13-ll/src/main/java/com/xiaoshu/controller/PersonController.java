package com.xiaoshu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
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
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.PersonService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("person")
public class PersonController extends LogController{

	@Autowired
	private OperationService operationService;
	@Autowired
	private PersonService ps;
	
	@RequestMapping("personIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{

		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<Company> list = ps.findCompany();
		request.setAttribute("list", list);
		return "person";
	}
	
	
	@RequestMapping(value="personList",method=RequestMethod.POST)
	public void userList(Vo v,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Vo> userList= ps.findVoPage(v, pageNum, pageSize);
			
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
	@RequestMapping("reservePerson")
	public void reserveUser(HttpServletRequest request,Person p,HttpServletResponse response){
		
		Integer userId = p.getId();
		JSONObject result=new JSONObject();
		List<Person> list = ps.findByName(p.getExpressName());
		try {
			if (userId != null) {   // userId不为空 说明是修改
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}else{
					p.setCreateTime(new Date());
					ps.updatePerson(p);
					result.put("success", true);
				}
				
			}else {   // 添加
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				} else {
					p.setCreateTime(new Date());
					ps.addPerson(p);
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
	
	
	@RequestMapping("deletePerson")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				ps.deletePerson(Integer.parseInt(id));
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
	@RequestMapping("dc")
	public void dc(HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject result=new JSONObject();
		
		HSSFWorkbook hw = new HSSFWorkbook();
		HSSFSheet sheet = hw.createSheet();
		String[] str = {"用户编号","人员名字","人员性别","人员特点"
				,"入职时间","所属公司","创建时间"};
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			row.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ps.findAll();
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow hr = sheet.createRow(i+1);
			hr.createCell(0).setCellValue(v.getId());
			hr.createCell(1).setCellValue(v.getExpressName());
			hr.createCell(2).setCellValue(v.getSex());
			hr.createCell(3).setCellValue(v.getExpressTrait());
			hr.createCell(4).setCellValue(TimeUtil.formatTime(v.getEntryTime(),"yyyy-MM-dd"));
			hr.createCell(5).setCellValue(v.getCname());
			hr.createCell(6).setCellValue(TimeUtil.formatTime(v.getCreateTime(),"yyyy-MM-dd"));
		}
		File file = new File("D:/电子书 音乐/图片/员工管理.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("dr")
	public void dr(MultipartFile xls,HttpServletRequest request,HttpServletResponse response) throws Exception{
		JSONObject result=new JSONObject();
		
		HSSFWorkbook hw = new HSSFWorkbook(xls.getInputStream());
		HSSFSheet sheet = hw.getSheetAt(0);
		int last = sheet.getLastRowNum();
		for (int i = 1; i <= last; i++) {
			Person p = new Person();
			HSSFRow row = sheet.getRow(i);
			p.setExpressName(row.getCell(1).getStringCellValue());
			p.setSex(row.getCell(2).getStringCellValue());
			p.setExpressTrait(row.getCell(3).getStringCellValue());
			p.setEntryTime(TimeUtil.ParseTime(row.getCell(4).getStringCellValue(),"yyyy-MM-dd"));
			
			String cname = row.getCell(5).getStringCellValue();
			Company c = ps.findByCompany(cname);
			if(c==null){
				Company c1 = new Company();
				c1.setExpressName(cname);
				ps.addCompany(c1);
				Company c2 = ps.findByCompany(cname);
				p.setExpressTypeId(c2.getId());
			}else {
				p.setExpressTypeId(c.getId());
			}
			p.setCreateTime(TimeUtil.ParseTime(row.getCell(6).getStringCellValue(),"yyyy-MM-dd"));
			
			ps.addPerson(p);
		}
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}
	// echarts
	@RequestMapping("echarts")
	public void echarts(HttpServletRequest request,HttpServletResponse response){
		List<Vo> v = ps.findTj();
		Object json = JSONObject.toJSON(v);
		WriterUtil.write(response, json.toString());
	}
	
}
