package com.zxy.demo.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.Message;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;

public class Producter {

	public static void main(String[] args) throws IOException, TimeoutException {
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
		String routingkey = "mq.topic";
		
//		循环发送多条消息		
		for(int i = 0 ;i<5;i++){
			String body = "hello rabbitmq!===============ACK&重回队列,第"+i+"条";
			Map<String,Object> head = new HashMap<>();
			head.put("n", i);
			BasicProperties properties = new BasicProperties(null, "utf-8", head, 2, 1, null, null, null, null, null, null, null, null, null);
			
		channel.basicPublish(exchange001, routingkey, properties, body.getBytes());
	}
		
	}

}
