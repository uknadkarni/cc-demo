package com.pivotal.example.xd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CcApplication {
	static ApplicationContext ctx = null;
	
	public CcApplication() {

	}
	
	public static ApplicationContext getApplicationContext(){
		if(ctx == null){
			ctx = SpringApplication.run(CcApplication.class);
		}
		return ctx;
	}
	
	public static void main(String[] args) {
		ctx = SpringApplication.run(CcApplication.class);
	}

}
