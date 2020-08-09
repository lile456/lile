package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.SchoolMapper;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.School;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class StudentService {

	@Autowired
	private StudentMapper sm;
	@Autowired
	private SchoolMapper cm;

	
	// 新增
	public void addStudent(Student t) throws Exception {
		sm.insert(t);
	};

	// 修改
	public void updateStudent(Student t) throws Exception {
		sm.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteStudent(Integer id) throws Exception {
		sm.deleteByPrimaryKey(id);
	};

	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = sm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<School> findSchool() {
		
		return cm.selectAll();
	}

	public List<Student> findStudent(String name) {
	
		return sm.findStudent(name);
	}
	// 导出
	public List<Vo> findAll() {
		
		return sm.findAll(null);
	}

	public School findByName(String name) {
		
		return cm.findByName(name);
	}

	public void addByName(School c1) {
		cm.insert(c1);
		
	}


}
