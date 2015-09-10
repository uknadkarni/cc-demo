package com.pivotal.example.xd;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;


public class RabbitClient {
	
	
	private static Logger logger = Logger.getLogger(RabbitClient.class);
	private String rabbitURI;
	private String exchangeName;
	private String queueName;
	private String vHost;
	private String host;
	private long port;
	
	@Override
	public String toString() {
		return "RabbitClient [rabbitURI=" + rabbitURI + ", exchangeName="
				+ exchangeName + ", queueName=" + queueName + ", vHost="
				+ vHost + ", host=" + host + ", port=" + port + "]";
	}


	
	
	public RabbitClient(ConnectionFactory connectionFactory) {
		logger.info("In RabbitClient constructor");		
		vHost = connectionFactory.getVirtualHost();
		host = connectionFactory.getHost();
		port = connectionFactory.getPort();

	}
	
	public String getRabbitURI(){
		String rabbitURI = "Needs implementation";
		return rabbitURI;
	}

}
