package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.Type;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.DeviceService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("device")
public class DeviceController extends LogController{
	@Autowired
	private OperationService operationService;
	@Autowired
	private DeviceService ds;
	
	@RequestMapping("deviceIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<Type> list = ds.findType();
		request.setAttribute("list", list);
		return "device";
	}
	
	
	@RequestMapping(value="deviceList",method=RequestMethod.POST)
	public void userList(HttpServletRequest request,HttpServletResponse response,String offset,String limit,Vo v) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Vo> vList= ds.findDevicePage(v, pageNum, pageSize);
			
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
	@RequestMapping("reserveDevice")
	public void reserveUser(HttpServletRequest request,Device d,HttpServletResponse response){
		Integer did = d.getDeviceid();
		JSONObject result=new JSONObject();
		try {
			if (did != null) {   // userId不为空 说明是修改
				List<Device> list = ds.findByName(d.getDevicename());
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}else{
					ds.updateDevice(d);
					result.put("success", true);
				}
				
			}else {   // 添加
				List<Device> list = ds.findByName(d.getDevicename());
				if(list.size()>0){  // 没有重复可以添加
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				} else {
					ds.addDevice(d);
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
	
	
	@RequestMapping("deleteDevice")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				ds.deleteDevice(Integer.parseInt(id));
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
	System.out.println("1");
		HSSFWorkbook hw = new HSSFWorkbook();
		HSSFSheet sheet = hw.createSheet();
		String[] str = {"编号","设备名称","设备类型名称","内存","机身颜色"
				,"价格","设备状态","创建时间"};
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < str.length; i++) {
			row.createCell(i).setCellValue(str[i]);
		}
		List<Vo> list = ds.findAll();
		for (int i = 0; i < list.size(); i++) {
			Vo v = list.get(i);
			HSSFRow hr = sheet.createRow(i+1);
			hr.createCell(0).setCellValue(v.getDeviceid());
			hr.createCell(1).setCellValue(v.getDevicename());
			hr.createCell(2).setCellValue(v.getTypename());
			hr.createCell(3).setCellValue(v.getDeviceram());
			hr.createCell(4).setCellValue(v.getColor());
			hr.createCell(5).setCellValue(v.getPrice());
			hr.createCell(6).setCellValue(v.getStatus());
			hr.createCell(7).setCellValue(TimeUtil.formatTime(v.getCreatetime(),"yyyy-MM-dd"));
		}
		File file = new File("d:/设备管理.xls");
		OutputStream os = new FileOutputStream(file);
		hw.write(os);
		hw.close();
		os.close();
		
		result.put("success", true);
		WriterUtil.write(response, result.toString());
	}

}
