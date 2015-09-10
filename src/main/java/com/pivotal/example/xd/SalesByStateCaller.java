package com.pivotal.example.xd;

import java.util.Map;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

@OnRegion(region="Orders", id="salesByStateCaller")
public interface SalesByStateCaller {

	@FunctionId("sumSales")
	public Map<String, Double> sumSales();
}
