package com.xiaoshu.controller;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

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
			PageInfo<Vo> userList= ps.findUserPage(v, pageNum, pageSize);
			
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
	// 新增addBank
	@RequestMapping("addBank")
	public void addBank(HttpServletRequest request,Bank b,HttpServletResponse response){
		
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
	@RequestMapping("dr")
	public void dr(HttpServletRequest request,HttpServletResponse response,MultipartFile xls) throws Exception{
		JSONObject result=new JSONObject();
		
		HSSFWorkbook hw = new HSSFWorkbook(xls.getInputStream()); 
		HSSFSheet sheet = hw.getSheetAt(0);
		int last = sheet.getLastRowNum();
		for (int i = 1; i <= last; i++) {
			Person p = new Person();
			HSSFRow row = sheet.getRow(i);
			p.setpName(row.getCell(1).getStringCellValue());
			p.setpSex(row.getCell(2).getStringCellValue());
			p.setpLike(row.getCell(3).getStringCellValue());
			p.setpCount(row.getCell(4).getStringCellValue());
			p.setpAge((int)row.getCell(5).getNumericCellValue());
			p.setCreatetime(TimeUtil.ParseTime(row.getCell(6).getStringCellValue(),"yyyy-MM-dd"));
			String bName = row.getCell(7).getStringCellValue();
			Bank b = ps.findByName(bName);
			p.setbId(b.getbId());
			ps.addPerson(p);
		}
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}
	// echarts
	@RequestMapping("echarts")
	public void echarts(HttpServletRequest request,HttpServletResponse response){
		List<Vo>  list = ps.findTj();
		Object json = JSONObject.toJSON(list);
		WriterUtil.write(response, json.toString());
	}
	
}
