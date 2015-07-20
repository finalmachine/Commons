package com.gbi.commons.net.amqp;

import com.gbi.commons.net.amqp.MsgConsumer;
import com.gbi.commons.net.amqp.MsgProducer;
import com.gbi.commons.net.amqp.MsgWorker;

public class MessageTest {
	public static void main(String[] args) throws Exception {
		MsgProducer<String> producer = new MsgProducer<>("MessageTest");
		producer.send("01");
		producer.send("12");
		producer.send("23");
		producer.send("34");
		producer.send("45");
		producer.send("56");
		producer.send("67");
		producer.send("78");
		producer.send("89");
		producer.send("90");
		producer.close();
		new Thread(new MsgConsumer("MessageTest", new MyWorker())).start();
	}
}

class MyWorker implements MsgWorker<String> {

	@Override
	public boolean work(String message) {
		System.out.println(message);
		return true;
	}
	
}