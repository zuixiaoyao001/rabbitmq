package com.zxy.demo.rabbitmq;

public class MessageDelegate{
/*	作为适配器该方法名是固定的，遵循适配器的规则，查看源码类MessageListenerAdapter定义如下：
	public static final String ORIGINAL_DEFAULT_LISTENER_METHOD = "handleMessage";*/
//	需要注意的是发送的消息如果没有转换器，则需要对应形参类型否则报错如下：Failed to invoke target method 'handleMessage' with 
//	argument type = [class java.lang.String], value = [{}]的形式
/*	public void handleMessage(byte[] body) {
		System.out.println("默认的方法："+new String(body));
	}*/
	
	public void modifyNameMessage(byte[] body) {
		System.out.println("修改默认方法为modigyNameMessage："+new String(body));
	}
}
