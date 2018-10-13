package com.zxy.demo.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Receiver {

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		// TODO Auto-generated method stub
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.10.110");
		factory.setPort(5672);
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();
		String exchange001 = "exchange_001";
		String queue001 = "queue_001";
		String routingkey = "mq.*";
		channel.exchangeDeclare(exchange001, "topic", true, false, null);
		channel.queueDeclare(queue001, true, false, false, null);
		channel.queueBind(queue001, exchange001, routingkey);
//		自定义消费者
		MyConsumer myConsumer = new MyConsumer(channel);
//		进行消费，签收模式一定要为手动签收
		channel.basicConsume(queue001, false, myConsumer);
	}

}
