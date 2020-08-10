package com.xiaoshu.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CourseMapper;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.entity.Course;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.Vo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class StudentService {

	@Autowired
	private StudentMapper sm;
	@Autowired
	private CourseMapper cm;
	@Autowired
	private JedisPool jp;

	
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

	public List<Course> findCourse() {
		return cm.selectAll();
	}

	public List<Vo> findAll() {
		
		return sm.findAll(null);
	}

	public List<Vo> findEcharts() {
		
		return sm.findEcharts();
	}
	// 部门添加
	public void addCourse(Course c) {
		cm.insert(c);
		Course co = cm.findByName(c.getName());
		Jedis jedis = jp.getResource();
		jedis.hset("key", co.getId()+"", co.toString());
		
	}
	// 部门效验
	public List<Course> findBycode(String code) {
		return cm.findBycode(code);
	}


	


}
