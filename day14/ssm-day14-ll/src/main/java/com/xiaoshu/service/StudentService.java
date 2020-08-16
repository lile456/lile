package com.xiaoshu.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.dao.TeacherMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.Teacher;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class StudentService {

	@Autowired
	private StudentMapper sm;
	@Autowired
	private TeacherMapper tm;
	@Autowired
	private JmsTemplate jt;
	
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

	public PageInfo<Vo> findUserPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = sm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<Teacher> findTeacher() {
		
		return tm.selectAll();
	}

	public List<Student> findByName(Integer integer) {
		
		return sm.findByName(integer);
	}

	public List<Vo> findTj() {
		
		return sm.findTj();
	}
	// 部门添加
	public void addTeacher(Teacher t) {
		tm.insert(t);
		final Teacher te = tm.findByTeacher(t.getName());
		
		jt.send("teacher",new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				String json = JSONObject.toJSONString(te);
				return session.createTextMessage(json);
			}
		});
	}
	/*public void jtMq(final Teacher te){
		jt.send("teacher",new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				String json = JSONObject.toJSONString(te);
				System.out.println(json+"**********");
				return session.createTextMessage(json);
			}
		
	}*/


}
