package com.zxy.demo.config;

import java.util.UUID;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;
import com.zxy.demo.rabbitmq.MessageDelegate;


@Configuration
@ComponentScan(basePackages= {"com.zxy.demo.*"})
public class RabbitmqCofing {
//	注入连接工厂，spring的配置，springboot可以配置在属性文件中
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connection = new CachingConnectionFactory();
		connection.setAddresses("192.168.10.110:5672");
		connection.setUsername("guest");
		connection.setPassword("guest");
		connection.setVirtualHost("/");
		return connection;
	}
//	配置RabbitAdmin来管理rabbit
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		//用RabbitAdmin一定要配置这个，spring加载的是后就会加载这个类================特别重要
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
//===========================以上结合测试rabbitAdmin部分===========================================================	
	
	
	
//===========================以下为AMQP配置队列绑定等，spring容器加载时候就能够注入===========================================================	
//	采用AMQP定义队列、交换器、绑定等
	@Bean(name="direct.queue01")
	public Queue queue001() {
		return new Queue("direct.queue01", true, false, false);
	}
	@Bean(name="test.direct01")
	public DirectExchange directExchange() {
		return new DirectExchange("test.direct01", true, false, null);
	}
	@Bean
	public Binding bind001() {
		return BindingBuilder.bind(queue001()).to(directExchange()).with("mq.#");
	}
	@Bean(name="topic.queue01")
	public Queue queue002() {
		return new Queue("topic.queue01", true, false, false);
	}
	@Bean(name="test.topic01")
	public TopicExchange topicExchange() {
		return new TopicExchange("test.topic01", true, false, null);
	}
	@Bean
	public Binding bind002() {
		return BindingBuilder.bind(queue002()).to(topicExchange()).with("mq.topic");
	}
	@Bean(name="fanout.queue01")
	public Queue queue003() {
		return new Queue("fanout.queue", true, false, false);
	}
	@Bean(name="test.fanout01")
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange("test.fanout01", true, false, null);
	}
	@Bean
	public Binding bind003() {
		return BindingBuilder.bind(queue003()).to(fanoutExchange());
	}
	
	
	
//===========================注入rabbitTemplate组件===========================================================	
//	跟spring整合注入改模板，跟springboot整合的话只需要在配置文件中配置即可
	@Bean 
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		return rabbitTemplate;
	}

	
	
//	添加SimpleMessageListenerContainer容器
	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
//		不要忘记connectionFactory哦！
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
//		添加监听的队列
		container.addQueues(queue001(),queue002(),queue003());
//		设定确认模式
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
//		设置最低消费者数量
		container.setConcurrentConsumers(1);
//		设置最大消费者数量
		container.setMaxConcurrentConsumers(5);
//		设置消费标签生成策略
		container.setConsumerTagStrategy(new ConsumerTagStrategy() {
			
			@Override
			public String createConsumerTag(String queue) {
				// TODO Auto-generated method stub
				return queue+UUID.randomUUID();
			}
		});
//		设置监听
		/*container.setMessageListener(new ChannelAwareMessageListener() {
			
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				String body = new String(message.getBody());
				System.out.println("==================消费的消息是："+body+"=========================");
				
			}
		});*/
//		设置适配器,MessageDelegate为自定义的适配器处理类
		MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//		设置默认方法名，不采用handleMessager方法，若不设置则用默认方法名的方法处理
		adapter.setDefaultListenerMethod("modifyNameMessage");
		container.setMessageListener(adapter);
		
		return container;
	}
}
