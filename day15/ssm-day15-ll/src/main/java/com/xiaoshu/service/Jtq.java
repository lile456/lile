package com.xiaoshu.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.xiaoshu.dao.BankMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Bank;

public class Jtq implements MessageListener{
	@Autowired
	private BankMapper bm;
	@Autowired
	private PersonService ps;
	@Override
	public void onMessage(Message message) {
		TextMessage m = (TextMessage) message;
		try {
			
			Bank b = ps.findById(m.getText());
			System.out.println("Mq++++"+b.toString());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
