package com.pivotal.example.xd;

//import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import com.gemstone.gemfire.cache.client.ClientCache;


@ComponentScan
@Configuration
@EnableGemfireRepositories
// @ContextConfiguration("file:src/main/resources/order-client-cache.xml")
@ImportResource("file:src/main/resources/order-client-cache.xml")
public class CcConfiguration {
	// private static Logger logger = Logger.getLogger(CcConfiguration.class);
	// final static String routingKey = "routing-key";
	final static String queueName = "myQueue";
	final static String exchangeName = "myExchange";

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private OrderRepository orderRepository;
	
	 @Autowired
	ClientCache cache;

	// private MessageListenerAdapter listenerAdapter;

	@Bean
	public ConnectionFactory connectionFactory() {
		// Read the host, port, username and password from a config file
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
				"localhost");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		return connectionFactory;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		// The routing key is set to the name of the queue by the broker for the
		// default exchange.
		template.setRoutingKey(queueName);
		// Where we will synchronously receive messages from
		template.setQueue(queueName);
		return template;
	}

	@Bean
	// Every queue is bound to the default direct exchange
	public Queue queue() {
		return new Queue(queueName);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(exchangeName);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(new MessageListenerAdapter(
				new OrderMessageHandler()));
		return container;
	}

	public CcConfiguration() {
		// TODO Auto-generated constructor stub
	}

}
