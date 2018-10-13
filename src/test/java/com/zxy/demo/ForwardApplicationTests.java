package com.zxy.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ForwardApplicationTests {

	@Test
	public void contextLoads() {
	}
	@Autowired
	private RabbitAdmin rabbitAdmin;
	@Test
	public void testAdmin() {
//		切记命名不能重复复
		rabbitAdmin.declareQueue(new Queue("test.direct.queue"));
		rabbitAdmin.declareExchange(new DirectExchange("test.direct"));
		rabbitAdmin.declareBinding(new Binding("test.direct.queue", Binding.DestinationType.QUEUE, "test.direct", "mq.direct", null));

		rabbitAdmin.declareQueue(new Queue("test.topic.queue", true,false, false));
		rabbitAdmin.declareExchange(new TopicExchange("test.topic", true,false));
//		如果注释掉上面两句实现声明，直接进行下面的绑定竟然不行，该版本amqp-client采用的是5.1.2,将上面两行代码放开，则运行成功
		rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("test.topic.queue", true,false, false))
				.to(new TopicExchange("test.topic", true,false)).with("mq.topic"));
//		经过实验确实是需要先声明，才可以运行通过
		rabbitAdmin.declareQueue(new Queue("test.fanout.queue",true,false,false,null));
		rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", true, false, null));
		rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("test.fanout.queue", true, false,false))
				.to(new FanoutExchange("test.fanout", true, false)));
		rabbitAdmin.purgeQueue("test.direct.queue", false);//清空队列消息
	}
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Test
	public void testTemplate() {
		String body = "hello,test rabbitTemplage!";
		MessageProperties properties = new MessageProperties();
		properties.setContentEncoding("utf-8");
		properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		properties.setPriority(1);
		properties.setHeader("nihao:", "yes!");
		Message message = new Message(body.getBytes(), properties);
//		MessagePostProcessor参数是在消息发送过程中动态修改消息属性的类
		rabbitTemplate.convertAndSend("test.direct01", "mq.direct", message,new MessagePostProcessor() {
			
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
//				修改属性
				message.getMessageProperties().setHeader("nihao:", "no");
//				添加属性
				message.getMessageProperties().setHeader("新添加属性：", "添加属性1");
				return message;
			}
		});
		
		
//		发送objcet类型
		rabbitTemplate.convertAndSend("test.topic01", "mq.topic", "send object type message!!!".getBytes());
		rabbitTemplate.convertAndSend("test.fanout01", "send object type message!!! to fanout.".getBytes());
		System.out.println("发送完毕！！！");
	}

}
