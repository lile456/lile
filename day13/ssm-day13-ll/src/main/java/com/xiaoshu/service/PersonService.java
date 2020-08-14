package com.xiaoshu.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.Vo;

@Service
public class PersonService {

	@Autowired
	private PersonMapper pm;
	@Autowired
	private CompanyMapper cm;
	@Autowired
	private RedisTemplate rt;
	@Autowired
	private JmsTemplate jt;
	
	// 新增
	public void addPerson(Person t) throws Exception {
		pm.insert(t);
		rt.delete("key");
		jtMQ(t.toString());
		
	};

	// 修改
	public void updatePerson(Person t) throws Exception {
		pm.updateByPrimaryKeySelective(t);
		rt.delete("key");
	};

	// 删除
	public void deletePerson(Integer id) throws Exception {
		pm.deleteByPrimaryKey(id);
		rt.delete("key");
	};

	public PageInfo<Vo> findVoPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> list = (List<Vo>) rt.boundHashOps("key").get("list");
		if (list == null) {
			 list = pm.findAll(v);
			 rt.boundHashOps("key").put("list",list);
			 System.out.println("数据库查询");
		}else {
			System.out.println("缓存查询");
		}
		
		
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(list);
		return pageInfo;
	}

	public List<Company> findCompany() {
		
		return cm.selectAll();
	}

	public List<Person> findByName(String pname) {
		
		return pm.findByName(pname);
	}

	public List<Vo> findTj() {
		
		return pm.findTj();
	}

	public List<Vo> findAll() {
	
		return pm.findAll(null);
	}

	public Company findByCompany(String cname) {
	
		return cm.findByCompany(cname);
	}

	public void addCompany(Company c1) {
		cm.insert(c1);
	}
	//MQ
	public void jtMQ(final String message ) {
		jt.send("mangran",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createTextMessage(message);
			}
		});
	}
	
	

}
