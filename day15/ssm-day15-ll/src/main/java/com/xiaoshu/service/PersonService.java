package com.xiaoshu.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
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
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class PersonService {

	@Autowired
	private PersonMapper pm;
	@Autowired
	private BankMapper bm;
	@Autowired
	private JmsTemplate jt;

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

	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> userList = pm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}
	// 查部门
	public List<Bank> findBank() {
	
		return bm.selectAll();
	}

	public List<Vo> findAll() {
		
		return pm.findAll(null);
	}

	public List<Vo> findTj() {
	
		return pm.findTj();
	}
	// 添加Bank

	public void addBank(Bank b) {
		bm.insert(b);
		Bank bid = bm.findByname(b.getbName());
		jtMq(bid.getbId()+"");
	}

	public void jtMq(final String bId){
		jt.send("ssm17",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createTextMessage(bId);
			}
		});
	}

	public Bank findById(String text) {
		
		Bank b = bm.findById(Integer.parseInt(text));
		return b;
	}

}
