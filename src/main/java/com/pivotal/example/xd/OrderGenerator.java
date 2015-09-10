package com.pivotal.example.xd;

import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class OrderGenerator implements Runnable {
	
	private static Logger logger = Logger.getLogger(OrderGenerator.class);
	private boolean generating = false;
	private boolean stopped = false;
	private Random random = new Random();

	public OrderGenerator() {
		// logger.info("Creating OrderGenerator");
	}

	public void startGen() {
		logger.info("Starting generation...");
		this.generating = true;
	}

	public void stopGen() {
		logger.info("Stopping generation");
		this.generating = false;
	}

	@Override
	public void run() {
		// logger.info("Running...");
		while (!stopped) {
			if (generating) {
				Order order = generateOrder();
				// logger.info("Sending order: " + order.toString());

				// Create Message
				MessageProperties mp = new MessageProperties();
				mp.setMessageId(order.getTransactionId());
				Message message = new Message(order.toBytes(), mp);

				// Send order
				ApplicationContext ctx = CcApplication.getApplicationContext();
				RabbitTemplate rabbitTemplate = ctx
						.getBean(RabbitTemplate.class);
				rabbitTemplate.convertAndSend(message);
				
				
				OrderRepository orderRepository = ctx.getBean(OrderRepository.class);
				orderRepository.save(order);
				logger.info("Sent order: " + order.toString());
			} else {
				 logger.info("Not generating...");
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					return;
				}
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				return;
			}
		}

	}

	public void shutdown() {
		stopped = true;

	}

	public static String[] creditCardTypes = new String[] { "MA", "VISA",
			"AmEx" };

	public static String[] retailers = new String[] { "Wal-Mart", "Kroger",
			"Target", "Walgreen", "The Home Depot", "Costco", "CVS Caremark",
			"Lowe's", "Best Buy", "Sears Holdings", "Safeway", "SUPERVALU",
			"Rite Aid", "Publix", "Macy's", "Ahold USA / Royal Ahold",
			"McDonald's", "Delhaize America", "Amazon.com", "Kohl's",
			"Apple Stores / iTunes", "J.C. Penney", "YUM! Brands", "TJX",
			"Meijer", "True Value", "H-E-B", "Dollar General", "ShopRite",
			"Gap", "BJ'S Wholesale Club", "Subway",
			"Wendy's / Arby's Restaurants", "Nordstrom", "Staples",
			"Ace Hardware", "Toys R Us", "Whole Foods Markets",
			"Bed Bath & Beyond", "7-Eleven", "Burger King Holdings", "Aldi",
			"Army Air Force Exchange", "Limited Brands", "A&P", "Menard",
			"Verizon Wireless", "Family Dollar", "Ross Stores",
			"Darden Restaurants", "Starbucks", "Office Depot",
			"Winn-Dixie Stores", "Hy-Vee", "Trader Joe's", "GameStop",
			"Giant Eagle", "AutoZone", "Dillard's", "DineEquity",
			"Advance Auto Parts", "Dollar Tree", "Barnes & Noble", "OfficeMax",
			"Wegman's Food Markets", "O'Reilly Automotive", "QVC",
			"Defense Commissary Agy.", "AT&T Wireless", "Save Mart", "Dell",
			"Big Lots", "PetSmart", "RadioShack", "Alimentation Couche-Tard",
			"Dick's Sporting Goods", "Albertsons", "WinCo Foods",
			"Sherwin-Williams", "Ruddick Corp.", "Neiman Marcus",
			"Michaels Stores", "Burlington Coat Factory", "Tractor Supply Co.",
			"Stater Bros. Holdings", "Foot Locker", "Belk",
			"Price Chopper Supermkts.", "IKEA North America",
			"Williams-Sonoma", "Sports Authority", "SonyStyle", "Raley's",
			"OSI Restaurant Partners", "Ingles Markets",
			"Brinker International", "HSN", "Bon-Ton Stores",
			"Abercrombie & Fitch", "ShopKo Stores" };

	private String generateCreditCardNumber() {
		int Low = 1000000000;
		int High = Integer.MAX_VALUE;
		int R = random.nextInt(High - Low) + Low;
		return String.valueOf(R);
	}

	private double generateAmount() {
		int low = 1;
		int high = 1000;
		double amount = random.nextInt(high - low) + low;
		return amount;
	}

	private String generateStreet() {
		return new String("3495 Deer Creek Rd");
	}

	private String generateCity() {
		return new String("Palo Alto");
	}

	private int generateZip() {
		return 94304;
	}

	private String generateCountry() {
		return new String("U.S.A.");
	}

	private double generateLatitude() {
		return 37.4292;
	}

	private double generateLongitude() {
		return 122.1381;
	}

	private long generateTimestamp() {
		int low = 1430000000;
		int high = 1460000000;
		int R = random.nextInt(high - low) + low;
		return R;
	}

	private Order generateOrder() {

		String transactionId = UUID.randomUUID().toString();
		String creditCardType = creditCardTypes[random
				.nextInt(creditCardTypes.length)];
		String creditCardNumber = generateCreditCardNumber();
		String retailerName = retailers[random.nextInt(retailers.length)];
		double amount = generateAmount();
		String street = generateStreet();
		String city = generateCity();
		int zip = generateZip();
		String state = Constants.states[random.nextInt(Constants.states.length)];
		String country = generateCountry();
		double latitude = generateLatitude();
		double longitude = generateLongitude();
		long timestamp = generateTimestamp();
		Order o = new Order(transactionId, creditCardType, creditCardNumber,
				retailerName, amount, street, city, zip, state, country,
				latitude, longitude, timestamp);
		return o;
	}
}
