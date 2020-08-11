package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.GoodsMapper;
import com.xiaoshu.dao.TypeMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.Type;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class GoodsService {

	@Autowired
	private GoodsMapper gm;
	@Autowired
	private TypeMapper tm;
	@Autowired
	private RedisTemplate rt;

	
	// 新增
	public void addGoods(Goods t) throws Exception {
		gm.insert(t);
	};

	// 修改
	public void updateGoods(Goods t) throws Exception {
		gm.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteGoods(Integer id) throws Exception {
		gm.deleteByPrimaryKey(id);
	};

	
	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = gm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<Type> findType() {
		return tm.selectAll();
	}

	public List<Goods> findCode(int code) {
		
		return gm.findCode(code);
	}
	// 部门添加
	public void addType(Type t) {
		tm.insert(t);
		Type tid = tm.findById(t.getTypename());
		rt.boundHashOps("tid").put(tid.getId(),tid.toString());
	}

	public List<Vo> findTj() {
	
		return gm.findTj();
	}


}
