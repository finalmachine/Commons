package com.gbi.commons.net.http;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.gbi.commons.config.Params;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ProxiesHttpClient extends BasicHttpClient {

	private static ConcurrentLinkedQueue<String> proxypool = new ConcurrentLinkedQueue<>();
	private static ReadWriteLock lock = new ReentrantReadWriteLock(false);

	private String _tag = null;

	public ProxiesHttpClient(String tag) {
		if (tag == null || tag.trim().length() == 0) {
			throw new IllegalArgumentException("tag must be valid");
		}
		_tag = tag;
		lock.writeLock().lock();
		if (proxypool.size() == 0) {
			reloadProxyList();
		}
		lock.writeLock().unlock();
	}

	protected void getNextProxy() {
		lock.readLock().lock();
		String proxyStr = proxypool.element();
		super.setProxy(proxyStr.split(":")[0], proxyStr.split(":")[1]);
		lock.readLock().unlock();
	}
	
	public void removeCurrentProxy() {
		super.removeCurrentProxy();
		lock.writeLock().lock();
		proxypool.remove(_currentProxy);
		_currentProxy = null;
		if (proxypool.size() == 0) {
			reloadProxyList();
		}
		lock.writeLock().unlock();
	}

	/**
	 * 该方法必须在写锁开启的情况下使用
	 */
	protected void reloadProxyList() {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient(Params.MongoDB.PROXIES.host, Params.MongoDB.PROXIES.port);
		} catch (UnknownHostException e) {
			System.err.println("mongo proxies unreachable");
		}
		DBObject query = new BasicDBObject();
		if (_tag != null) {
			query.put("tag", _tag);
		}
		DBObject key = new BasicDBObject().append("_id", 1);
		DBCursor cursor = mongo.getDB(Params.MongoDB.PROXIES.database).getCollection("proxies").find(query, key);
		for (DBObject o : cursor) {
			proxypool.add((String) o.get("_id"));
		}
		cursor.close();
		mongo.close();
		if (proxypool.size() == 0) {
			throw new RuntimeException("no useful proxy in mongo");
		}
		System.out.println("reload proxy list form db, reload count: " + proxypool.size());
	}
}
