package com.gbi.commons.net.amqp;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MsgConsumers {

	private static int consumersNum = 8;
	private static int MAX_QUEUQ_SIZE = 100;

	public static void main(String[] args) throws IOException, TimeoutException {
		ExecutorService es = new ThreadPoolExecutor(consumersNum, consumersNum, 10000L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(MAX_QUEUQ_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());
		Connection conn = new ConnectionFactory().newConnection(es);
	}
}
