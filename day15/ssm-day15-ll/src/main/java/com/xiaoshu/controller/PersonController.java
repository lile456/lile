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
import com.xiaoshu.entity.Bank;
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
		List<Bank> list = ps.findBank();
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
		Integer userId = p.getpId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				ps.updatePerson(p);
				result.put("success", true);
			}else {   // 添加
				ps.addPerson(p);
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
	// 新增或修改
	@RequestMapping("Bank")
	public void Bank(HttpServletRequest request,Bank b,HttpServletResponse response){
		
		JSONObject result=new JSONObject();
		try {
				b.setCreatetime(new Date());
				ps.addBank(b);
				result.put("success", true);
			
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
		String[] str = {"人员编号","人员姓名","人员性别","人员爱好","存款金额"
					,"人员年龄","存款日期","存款银行"};
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			row.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ps.findAll();
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow hr = sheet.createRow(i+1);
			hr.createCell(0).setCellValue(v.getpId());
			hr.createCell(1).setCellValue(v.getpName());
			hr.createCell(2).setCellValue(v.getpSex());
			hr.createCell(3).setCellValue(v.getpLike());
			hr.createCell(4).setCellValue(v.getpCount());
			hr.createCell(5).setCellValue(v.getpAge());
			hr.createCell(6).setCellValue(TimeUtil.formatTime(v.getCreatetime(),"yyyy-MM-dd"));
			hr.createCell(7).setCellValue(v.getbName());
		}
		File file = new File("d://学生管理.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		
		WriterUtil.write(response, result.toString());
	}
	//echarts
	@RequestMapping("echarts")
	public void echarts(HttpServletRequest request,HttpServletResponse response){
		List<Vo> list = ps.findTj();
		Object json = JSONObject.toJSON(list);
		WriterUtil.write(response, json.toString());
	}
	
}
