package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.dao.EmpMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpExample;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class EmpService {

	@Autowired
	private EmpMapper em;
	@Autowired
	private DeptMapper dm;
	
	// 新增
	public void addEmp(Emp t) throws Exception {
		em.insert(t);
	};

	// 修改
	public void updateEmp(Emp t) throws Exception {
		em.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteEmp(Integer id) throws Exception {
		em.deleteByPrimaryKey(id);
	};

	
	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = em.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<Dept> findDept() {
	
		return dm.selectAll();
	}

	public List<Emp> findByName(Emp e) {
		
		return em.findByName(e);
	}


}
