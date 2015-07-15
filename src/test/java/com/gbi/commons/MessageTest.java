package com.gbi.commons;

import com.gbi.commons.net.amqp.MsgConsumer;
import com.gbi.commons.net.amqp.MsgConsumer2;
import com.gbi.commons.net.amqp.MsgProducer;
import com.gbi.commons.net.amqp.MsgWorker;

public class MessageTest {
	public static void main(String[] args) throws Exception {
		MsgProducer<String> producer = new MsgProducer<>("MessageTest");
		producer.send("123");
		producer.send("456");
		new Thread(new MsgConsumer2("MessageTest", new MyWorker())).start();
		producer.close();
	}
}

class MyWorker implements MsgWorker<String> {

	@Override
	public boolean work(String message) {
		System.out.println(message);
		return true;
	}
	
}