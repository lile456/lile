package com.xiaoshu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
import com.xiaoshu.entity.Major;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.Stu;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.StuService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("stu")
public class StuController extends LogController{
	
	@Autowired
	private OperationService operationService;
	@Autowired
	private StuService ss;
	
	@RequestMapping("stuIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<Major> list = ss.findMajor();
		request.setAttribute("list", list);
		return "stu";
	}
	
	
	@RequestMapping(value="stuList",method=RequestMethod.POST)
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
	@RequestMapping("reserveStu")
	public void reserveUser(HttpServletRequest request,Stu s,HttpServletResponse response){
		Integer userId = s.getSdId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				ss.updateStu(s);
				result.put("success", true);
			}else {   // 添加
				ss.addStu(s);
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
	// 新增专业
	@RequestMapping("addMajor")
	public void addMajor(HttpServletRequest request,Major m,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
				ss.addMajor(m);
				result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteStu")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				ss.deleteStu(Integer.parseInt(id));
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
		String[] str = {"学生编号","学生姓名","学生性别","学生爱好"
					,"学生生日","专业"};
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			row.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ss.findAll();
		int a = 1;
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow hr = sheet.createRow(a);
			if (v.getMdname().contains("大数据") && v.getSdhobby().contains("篮球")) {
				hr.createCell(0).setCellValue(v.getSdId());
				hr.createCell(1).setCellValue(v.getSdname());
				hr.createCell(2).setCellValue(v.getSdsex());
				hr.createCell(3).setCellValue(v.getSdhobby());
				hr.createCell(4).setCellValue(TimeUtil.formatTime(v.getSdbirth(),"yyyy-MM-dd"));
				hr.createCell(5).setCellValue(v.getMdname());
				a++;
			}
		}
		File file = new File("d:/学生管理.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		
		WriterUtil.write(response, result.toString());
	}
	// echarts
	@RequestMapping("echarts")
	public void echarts(HttpServletRequest request,HttpServletResponse response){
		List<Vo> list = ss.findTj();
		Object json = JSONObject.toJSON(list);
		WriterUtil.write(response, json.toString());
	}
	
}
