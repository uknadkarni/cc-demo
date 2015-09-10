package com.pivotal.example.xd;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderReceiver {
	private static Logger logger = Logger.getLogger(OrderReceiver.class);
	
	public OrderReceiver() {
		// TODO Auto-generated constructor stub
	}
	
	public void receiveMessage(String message){
		ApplicationContext context = new AnnotationConfigApplicationContext(CcConfiguration.class);
		AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
		System.out.println("Received: " + amqpTemplate.receiveAndConvert());
	}

}
