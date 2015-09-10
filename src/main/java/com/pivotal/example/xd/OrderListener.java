package com.pivotal.example.xd;

import com.gemstone.gemfire.cache.util.CacheListenerAdapter;

public class OrderListener  extends CacheListenerAdapter<String, Order>{

	public OrderListener() {
	}

}
