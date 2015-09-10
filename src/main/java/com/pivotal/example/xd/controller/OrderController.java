package com.pivotal.example.xd.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.pivotal.example.xd.CcApplication;
import com.pivotal.example.xd.Constants;
import com.pivotal.example.xd.HeatMap;
import com.pivotal.example.xd.Order;
import com.pivotal.example.xd.OrderGenerator;
import com.pivotal.example.xd.OrderRepository;
import com.pivotal.example.xd.SalesByStateCaller;
/**
 * Handles requests for the application home page.
 */
@Controller
public class OrderController {
	@Autowired
	ServletContext context;

	private static Map<String, Queue<Order>> stateOrdersMap = new HashMap<String, Queue<Order>>();


	boolean generatingData = false;

	static Logger logger = Logger.getLogger(OrderController.class);

	OrderGenerator generator = new OrderGenerator();
	Thread threadSender = new Thread(generator);

	public OrderController() {

		for (int i = 0; i < Constants.states.length; i++) {
			stateOrdersMap.put(Constants.states[i],
					new ArrayBlockingQueue<Order>(10));
		}

		threadSender.start();

	}

	private int getOrderSum(String state) {

		int sum = 0;
		Queue<Order> q = stateOrdersMap.get(state);
		Iterator<Order> it = q.iterator();
		while (it.hasNext()) {
			sum += it.next().getAmount();
		}

		return sum;
	}

	public static synchronized void registerOrder(Order order) {
		Queue<Order> orderQueue = stateOrdersMap.get(order.getState());
		if (!orderQueue.offer(order)) {
			orderQueue.remove();
			orderQueue.add(order);
		}
	}

	@RequestMapping(value = "/")
	public String home(Model model) throws Exception {
		//model.addAttribute("rabbitURI", client.getRabbitURI());

		ObjectMapper mapper = new ObjectMapper();

		// add details about VCAP APPLICATION
		if (System.getenv("VCAP_APPLICATION") != null) {
			Map vcapMap = mapper.readValue(System.getenv("VCAP_APPLICATION"),
					Map.class);
			model.addAttribute("vcap_app", vcapMap);
		}

		return "WEB-INF/views/pcfdemo.jsp";
	}

	@RequestMapping(value = "/getData")
	public @ResponseBody double getData(@RequestParam("state") String state) {
		logger.info("Getting data...");
		if (!stateOrdersMap.containsKey(state))
			return 0;
		Queue<Order> q = stateOrdersMap.get(state);
		if (q.size() == 0)
			return 0;
		Order[] orders = q.toArray(new Order[] {});
		return orders[orders.length - 1].getAmount();

	}
	/*
	@RequestMapping(value = "/startStream")
	public @ResponseBody String startStream() {
		logger.warn("Rabbit URI " + client.getRabbitURI());
		if (client.getRabbitURI() == null)
			return "Please bind a RabbitMQ service";

		if (generatingData)
			return "Data already being generated";

		generatingData = true;

		generator.startGen();
		return "Started";

	}
	
	@RequestMapping(value = "/stopStream")
	public @ResponseBody String stopStream() {
		logger.warn("Rabbit URI " + client.getRabbitURI());
		if (client.getRabbitURI() == null)
			return "Please bind a RabbitMQ service";

		if (!generatingData)
			return "Not Streaming";
		generatingData = false;
		generator.stopGen();

		return "Stopped";

	}
	*/
	@RequestMapping(value = "/killApp")
	public @ResponseBody String kill() {
		logger.warn("Killing application instance");
		System.exit(-1);
		return "Killed";

	}

	@RequestMapping(value = "/getHeatMap")
	public @ResponseBody HeatMap getHistograms() {
		HeatMap heatMap = new HeatMap();
		for (int i = 0; i < Constants.states.length; i++) {
			heatMap.addOrderSum(Constants.states[i],
					getOrderSum(Constants.states[i]));
		}

		heatMap.assignColors();
		return heatMap;

	}

	@RequestMapping(value = "/startStream")
	public @ResponseBody String startStream() {
		logger.info("Starting stream of orders...");
		if (generatingData)
			return "Data already being generated";

		generatingData = true;
		generator.startGen();
		return "Started";
	}

	@RequestMapping(value = "/stopStream")
	public @ResponseBody String stopStream() {
		logger.info("Stopping stream of orders...");
		if (!generatingData)
			return "Not Streaming";
		generatingData = false;
		generator.stopGen();

		return "Stopped";
	}

	@RequestMapping(value="/order", method=RequestMethod.GET)
	public @ResponseBody Order getTransaction(@RequestParam("transactionId")String transactionId, Map<String, Object> model){
		logger.info("getTransaction for " + transactionId);
		ApplicationContext ctx = CcApplication.getApplicationContext();
		OrderRepository or = ctx.getBean(OrderRepository.class);
		Order order = or.findByTransactionId(transactionId);
		if(order == null){
			logger.error("Order not found: " + transactionId);
			return new Order();
			// return "Order not found!";
		}
		else
		{
			logger.info("Found order: " + order);
			model.put("order", order);
			return order;
			// return order.toString();
		}
	}
	
	@RequestMapping(value="/ccType", method=RequestMethod.GET)
	public @ResponseBody Iterable<Order> getByCreditCardType(@RequestParam("creditCardType")String creditCardType, Map<String, Object> model){
		logger.info("getByCreditCardType for " + creditCardType);
		ApplicationContext ctx = CcApplication.getApplicationContext();
		OrderRepository or = ctx.getBean(OrderRepository.class);
		Iterable<Order> orders = or.findByCreditCardType(creditCardType);
		if(orders == null){
			logger.error("Orders not found for: " + creditCardType);
			Iterable<Order> o = new ArrayList<Order>();
			return o;
		}
		else
		{
			logger.info("Found orders for : " + creditCardType);
			model.put("orders", orders);
			return orders;
		}
	}

	@RequestMapping(value="/cache", method=RequestMethod.GET)
	public @ResponseBody ClientCache getCache(){
		logger.info("getCache");
		ApplicationContext ctx = CcApplication.getApplicationContext();
		ClientCache cc = ctx.getBean(ClientCache.class);
		if(cc == null){
			logger.error("client cache is null");
			return null;
		}
		else
		{
			logger.info("ClientCache : " + cc.toString());
			return cc;
		}
	}
	
	@RequestMapping(value="/salesByState", method=RequestMethod.GET)
	public @ResponseBody Map<String, Double> getSalesByState(){
		logger.info("getSalesByState");
		ApplicationContext ctx = CcApplication.getApplicationContext();
		SalesByStateCaller salesByStateCaller = ctx.getBean(SalesByStateCaller.class);
		return salesByStateCaller.sumSales();
	}

	@PreDestroy
	public void shutdownThread() {

		generator.shutdown();
	}

}
