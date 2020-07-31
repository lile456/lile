package com.xiaoshu.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentCategory;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.ContentService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("content")
public class ContentController extends LogController{
	@Autowired
	private OperationService operationService;
	@Autowired
	private ContentService cs;
	
	
	@RequestMapping("contentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		
		List<ContentCategory> list = cs.findCategory();
		request.setAttribute("list", list);
		
		return "content";
	}
	
	
	@RequestMapping(value="contentList",method=RequestMethod.POST)
	public void userList(HttpServletRequest request,HttpServletResponse response,String offset,String limit,Vo v) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Vo> vList= cs.findContentPage(v, pageNum, pageSize);
			
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",vList.getTotal() );
			jsonObj.put("rows", vList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveContent")
	public void reserveUser(HttpServletRequest request,Content c,HttpServletResponse response,MultipartFile photo){
		Integer cid = c.getContentid();
		JSONObject result=new JSONObject();
		List<Content> list = cs.findByName(c.getContenttitle());
		try {
			if (cid != null) {   // userId不为空 说明是修改
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}else{
					cs.updateContent(c);
					result.put("success", true);
				}
			}else {   // 添加
				
				if(list.size()>0){  // 没有重复可以添加
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				} else {
					if(photo.getSize()>0){
						String file = photo.getOriginalFilename();
						file = UUID.randomUUID().toString()+"jpg";
						photo.transferTo(new File("d:/photo/"+file));
						c.setPicpath(file);
					}
					cs.addContent(c);
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
	
	
	@RequestMapping("deleteContent")
	public void delContent(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				cs.deleteContent(Integer.parseInt(id));
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
		for (int i = 1; i < last; i++) {
			Content c = new Content();
			HSSFRow row = sheet.getRow(i);
			c.setContenttitle(row.getCell(1).getStringCellValue());
			
			//String categoryname = row.getCell(2).getStringCellValue();
			//ContentCategory g = cs.findC(categoryname);
			/*if(g==null){
				ContentCategory g1 = new ContentCategory();
				g1.setCategoryname(categoryname);
				cs.addCategory(g1);
				ContentCategory g2 = cs.findC(g1.getCategoryname());
				c.setContentcategoryid(g2.getContentcategoryid());
			}else {*/
				c.setContentcategoryid((int) row.getCell(2).getNumericCellValue());
			/*}*/
			c.setPicpath(row.getCell(3).getStringCellValue());
			c.setContenturl(row.getCell(4).getStringCellValue());
			c.setPrice((double)row.getCell(5).getNumericCellValue());
			c.setStatus(row.getCell(6).getStringCellValue());
			c.setCreatetime(new Date());
			cs.addConten(c);
		}
		
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}
	
}
