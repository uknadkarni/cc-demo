package com.pivotal.example.xd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.stereotype.Component;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;

@SuppressWarnings("serial")
@Component
public class SalesByStateExecutor extends FunctionAdapter {
	/*
	@Resource(name="Orders")
	private Region<String, Order> orderRegion;
*/
	@Autowired
	private OrderRepository orderRepository;
	
	private static Logger logger = Logger.getLogger(OrderGenerator.class);

	
	public SalesByStateExecutor() {
	}

	@Override
	public void execute(FunctionContext functionContext) {
		Map<String, Double> map = new HashMap<String, Double>();
		
		for(int ii = 0; ii < Constants.states.length; ii++){
			String state = Constants.states[ii];
			logger.info("Getting orders for state: " + state);
			Iterable<Order> ordersForState = orderRepository.findByState(state);
			Iterator<Order> it = ordersForState.iterator();
			Double totalSalesInState = 0.0;
			while(it.hasNext()){
				Order o = it.next();
				totalSalesInState = totalSalesInState + o.getAmount();
			}
			map.put(state, totalSalesInState);
		}
		functionContext.getResultSender().lastResult(map);
	}

	@Override
	public String getId() {
		return getClass().getSimpleName();
	}

}
