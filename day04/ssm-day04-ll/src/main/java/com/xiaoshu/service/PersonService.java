package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.Tj;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.entity.UserExample.Criteria;

@Service
public class PersonService {

	@Autowired
	private PersonMapper pm;
	@Autowired
	private CompanyMapper cm;
	
	// 新增
	public void addPerson(Person p) throws Exception {
		pm.insert(p);
	};

	// 修改
	public void updatePerson(Person p) throws Exception {
		pm.updateByPrimaryKeySelective(p);
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

	public List<Company> findCompany() {
		return cm.selectAll();
	}

	public List<Tj> findTj() {
		
		return pm.findTj();
	}


}
