package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;import com.xiaoshu.entity.Teacher;

public class Jtq implements MessageListener {

	@Override
	public void onMessage(Message message) {
		TextMessage m = (TextMessage) message;
		try {
			Teacher teacher = JSONObject.parseObject(m.getText(),Teacher.class);
			System.out.println(teacher);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
