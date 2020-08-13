package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.entity.Major;

public class Jtq implements MessageListener{
	@Autowired
	private RedisTemplate rt;
	@Override
	public void onMessage(Message message) {
		TextMessage tm = (TextMessage) message;
		try {
			Major major = JSONObject.parseObject(tm.getText(),Major.class);
			System.out.println(major);
			rt.boundHashOps("key").put(major.getMdname(), major.getMdId());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
