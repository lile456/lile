package com.xiaoshu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Type;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.service.GoodsService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("goods")
public class GoodsController extends LogController{
	@Autowired
	private OperationService operationService;
	@Autowired
	private GoodsService gs;
	
	@RequestMapping("goodsIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);
		List<Type> list = gs.findType();
		request.setAttribute("list", list);
		return "goods";
	}
	
	
	@RequestMapping(value="goodsList",method=RequestMethod.POST)
	public void userList(Vo v,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<Vo> userList= gs.findVoPage(v, pageNum, pageSize);
			
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
	@RequestMapping("reserveGoods")
	public void reserveUser(HttpServletRequest request,Goods g,HttpServletResponse response){
		Integer userId = g.getId();
		JSONObject result=new JSONObject();
		List<Goods> list = gs.findCode(g.getCode());
		try {
			if (userId != null) {   // userId不为空 说明是修改
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该编号被使用");
				}else{
					gs.updateGoods(g);
					result.put("success", true);
				}
				
			}else {   // 添加
				if(list.size()>0){
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				} else {
					g.setCreatetime(new Date());
					
					gs.addGoods(g);
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
	// 新增商品分类
	@RequestMapping("addType")
	public void addType(HttpServletRequest request,Type t,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
				t.setCreatetime(new Date());
				
				gs.addType(t);
				result.put("success", true);
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteGoods")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				gs.deleteGoods(Integer.parseInt(id));
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
	
	@RequestMapping("echarts")
	public void echarst(HttpServletRequest request,HttpServletResponse response){
		List<Vo> v = gs.findTj();
		
		Object json = JSONObject.toJSON(v);
		WriterUtil.write(response, json.toString());
	}
}
