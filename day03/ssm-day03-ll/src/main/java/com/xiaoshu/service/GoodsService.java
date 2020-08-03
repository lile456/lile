package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.GoodsMapper;
import com.xiaoshu.dao.GoodsTypeMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.GoodsType;
import com.xiaoshu.entity.Tj;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class GoodsService {

	@Autowired
	private GoodsMapper gm;
	@Autowired
	private GoodsTypeMapper tm;


	// 新增
	public void addGoods(Goods g) throws Exception {
		gm.insert(g);
	};

	// 修改
	public void updateGoods(Goods g) throws Exception {
		gm.updateByPrimaryKeySelective(g);
	};

	// 删除
	public void deleteGoods(Integer id) throws Exception {
		gm.deleteByPrimaryKey(id);
	};
	
	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> vList = gm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(vList);
		return pageInfo;
	}

	public List<GoodsType> findType() {
		
		return tm.selectAll();
	}

	public List<Tj> findTj() {
		
		return gm.findTj();
	}


}
