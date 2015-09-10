package com.pivotal.example.xd;



import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.pivotal.example.xd.controller.OrderController;

public class OrderMessageHandler {
	

	
	private static Logger logger = Logger.getLogger(OrderMessageHandler.class);
	public OrderMessageHandler(){
		
	}
	public void handleMessage(String text) {
		System.out.println("Received (text): " + text);
	}
	
    public void handleMessage(Map map) {
    	System.out.println("Received (map): " + map);
    }

    public void handleMessage(byte[] bytes) {
        // ApplicationContext ctx = CcApplication.getApplicationContext();
		// RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);
		// System.out.println("Received (bytes): " + (String)amqpTemplate.receiveAndConvert());
    	Order order = Order.fromBytes(bytes);
    	logger.info(" Received Order: " + order);
    	OrderController.registerOrder(order);   	
    }
    
    public void handleMessage(Serializable obj) {
    	System.out.println("Received (serializable): " + obj.toString());    	
    }
}