package com.gbi.commons.net.amqp;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public final class MsgConsumer extends MsgBase implements Runnable, Consumer {

	private MsgWorker<? extends Serializable> _worker = null;

	public <T extends Serializable> MsgConsumer(String queueName, MsgWorker<T> worker) throws IOException, TimeoutException,
			CloneNotSupportedException {
		super(queueName);
		_channel.basicQos(1);
		_worker = worker.cloneWorker();
	}

	public <T extends Serializable> MsgConsumer(String queueName, MsgWorker<T> worker, String host, int port, String username,
			String password, String virtualHost) throws IOException, TimeoutException,
			CloneNotSupportedException {
		super(queueName, host, port, username, password, virtualHost);
		_channel.basicQos(1);
		_worker = worker.cloneWorker();
	}

	@Override
	public void run() {
		try {
			_channel.basicConsume(_queueName, false, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
		System.out.println("handleConsumeOk");
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		System.out.println("handleCancelOk");
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		System.out.println("handleCancel");
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
			byte[] body) throws IOException {
		if (_worker.work(SerializationUtils.deserialize(body))) {
			_channel.basicAck(envelope.getDeliveryTag(), false);
		} else {
			System.err.println("job undo");
		}
	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		System.out.println("handleShutdownSignal");
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		System.out.println("handleRecoverOk");
	}
}
