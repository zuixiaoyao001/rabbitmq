package com.zxy.demo.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 可以继承，可以实现，实现的话要覆写的方法比较多，所以这里用了继承
 *
 */
public class MyConsumer extends DefaultConsumer{
	private Channel channel;
	public MyConsumer(Channel channel) {
		super(channel);
		// TODO Auto-generated constructor stub
		this.channel=channel;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		System.out.println("消费标签："+consumerTag);
		System.out.println("envelope.getDeliveryTag():==="+envelope.getDeliveryTag());
		System.out.println("envelope.getExchange():==="+envelope.getExchange());
		System.out.println("envelope.getRoutingKey():==="+envelope.getRoutingKey());
		System.out.println("body:==="+new String(body));
		System.out.println("===================休眠以便查看===============");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		手动签收
		Integer i = (Integer) properties.getHeaders().get("n");
		System.out.println("iiiiiiiiiiiiiiiii======================================================"+i);
		if(i==1) {
			channel.basicNack(envelope.getDeliveryTag(),false, true);//第三个参数为是否重返队列
		}else {
			channel.basicAck(envelope.getDeliveryTag(), false);	
		}
	}
	

}
