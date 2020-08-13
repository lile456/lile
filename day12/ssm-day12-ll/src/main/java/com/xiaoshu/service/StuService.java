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
import com.xiaoshu.dao.MajorMapper;
import com.xiaoshu.dao.StuMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Major;
import com.xiaoshu.entity.Stu;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class StuService {

	@Autowired
	private StuMapper sm;
	@Autowired
	private MajorMapper mm;
	@Autowired
	private JmsTemplate jt;
	
	// 新增
	public void addStu(Stu t) throws Exception {
		sm.insert(t);
	};

	// 修改
	public void updateStu(Stu t) throws Exception {
		sm.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteStu(Integer id) throws Exception {
		sm.deleteByPrimaryKey(id);
	};

	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = sm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	public List<Major> findMajor() {
	
		return mm.selectAll();
	}

	public List<Vo> findTj() {
		
		return sm.findTj();
	}

	public List<Vo> findAll() {
		
		return sm.findAll(null);
	}
	// Mq
	public void	jtMq(final Major m) {
		jt.send("h1909e",new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				String major = JSONObject.toJSONString(m);
				return session.createTextMessage(major);
			}
		});
		
	}

	public void addMajor(Major m) {
		mm.insert(m);
		Major  major = mm.findByName(m.getMdname());
		jtMq(major);
	}

	
}
