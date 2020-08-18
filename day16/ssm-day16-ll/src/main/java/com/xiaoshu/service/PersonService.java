package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.BankMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Bank;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class PersonService {

	@Autowired
	private PersonMapper pm;
	@Autowired
	private BankMapper bm;
	@Autowired
	private RedisTemplate	rt;
	
	// 新增
	public void addPerson(Person t) throws Exception {
		pm.insert(t);
	};

	// 修改
	public void updatePerson(Person t) throws Exception {
		pm.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deletePerson(Integer id) throws Exception {
		pm.deleteByPrimaryKey(id);
	};


	public PageInfo<Vo> findUserPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = pm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<Bank> findBank() {
	
		return bm.selectAll();
	}

	public List<Vo> findTj() {
		
		return pm.findTj();
	}

	public Bank findByName(String bName) {
		
		return bm.findByName(bName);
	}

	public void addBank(Bank b) {
		bm.insert(b);
		Bank bank = bm.findByName(b.getbName());
		rt.boundHashOps("key").put(bank.getbId(), bank.toString());
	}


}
