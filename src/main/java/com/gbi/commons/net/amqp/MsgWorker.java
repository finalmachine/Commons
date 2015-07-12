package com.gbi.commons.net.amqp;

import java.io.Serializable;

public interface MsgWorker<T extends Serializable> extends Cloneable {
	/**
	 * @return true if the work is done correctly
	 */
	public boolean work(T message);
	public MsgWorker<T> cloneWorker() throws CloneNotSupportedException;
}