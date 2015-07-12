package com.gbi.commons.net.amqp;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MsgBase implements Closeable {

	protected Connection _connection = null;
	protected Channel _channel = null;
	protected String _queueName = null;
	
	public MsgBase(String queueName) throws IOException, TimeoutException {
		_connection = new ConnectionFactory().newConnection();
		_channel = _connection.createChannel();
		_queueName = queueName;
		_channel.queueDeclare(queueName, true, false, false, null);
	}
	
	public MsgBase(String queueName, String host, int port, String username, String password,
			String virtualHost) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		if (host != null) {
			factory.setHost(host);
		}
		if (port > 0) {
			factory.setPort(port);
		}
		if (username != null) {
			factory.setUsername(username);
		}
		if (password != null) {
			factory.setPassword(password);
		}
		if (virtualHost != null) {
			factory.setVirtualHost(virtualHost);
		}
		_connection = factory.newConnection();
		_channel = _connection.createChannel();
		_queueName = queueName;
		_channel.queueDeclare(queueName, true, false, false, null);
	}

	@Override
	public void close() throws IOException {
		try {
			_channel.close();
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		} finally {
			_connection.close();
		}
	}
}
