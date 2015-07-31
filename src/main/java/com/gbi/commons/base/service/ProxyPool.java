package com.gbi.commons.base.service;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.gbi.commons.config.Params;
import com.gbi.commons.net.http.BasicHttpClient;
import com.gbi.commons.net.http.BasicHttpResponse;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.rabbitmq.client.AMQP.Basic;

public class ProxyPool {

	private static final Map<String, String> checkSubject = new HashMap<>();
	private static final Map<String, String> subjectMd5 = new HashMap<>();
	
	private static MongoClient client = null;
	private static DBCollection collection = null;
	
	private static void init() {
		try {
			client = new MongoClient(Params.MongoDB.PROXIES.host, Params.MongoDB.PROXIES.port);
			collection = client.getDB(Params.MongoDB.PROXIES.database).getCollection("proxies");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		checkSubject.put("CN", "http://www.baidu.com.cn/img/bd_logo1.png"); // 百度logo
		
		BasicHttpClient c = new BasicHttpClient();
		c.setProxy(Params.StableProxy.host, Params.StableProxy.port);
		c.close();
	}
	
	private static void exit() {
		client.close();
	}
	
	// 抓取有代理的代理服务器地址
	public static void GrabYoudaili() {
		BasicHttpClient browser = new BasicHttpClient();
		BasicHttpResponse response = browser.get("http://www.youdaili.net/Daili/guowai/");
		if (response == null) {
			browser.close();
			throw new RuntimeException("有代理国外代理访问失败");
		}
		int count = 0;
		// 抓取首页 >
		Elements lines = response.getDocument().select("ul.newslist_line>li>a");
		for (Element line : lines) {
			response = browser.get(line.absUrl("href"));
			if (response == null) {
				System.err.println("丢失一个网页");
				continue;
			}
			List<TextNode> textNodes = response.getDocument().select("div.cont_font>p").first().textNodes();
			Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)@([^@#]*)#(【匿】){0,1}([^#]*)");
			for (TextNode textNode : textNodes) {
				Matcher m = pattern.matcher(textNode.text());
				if (m.find()) {
					DBObject proxy = new BasicDBObject();
					proxy.put("_id", m.group(1) + ":" + m.group(2));
					proxy.put("IPv4", m.group(1));
					proxy.put("port", m.group(2));
					proxy.put("protocol", m.group(3));
					proxy.put("type", m.group(4) == null ? "" : "anonymous");
					proxy.put("location", m.group(5));
					collection.save(proxy);
					++count;
				}
			}
		}
		// 抓取首页 <
		System.out.println("网站:有代理 共捕获数据 " + count + " 条");
		browser.close();
	}
	
	public static void checkProxyPool() {
		BasicHttpClient browser = new BasicHttpClient();
		DBCursor cursor = collection.find();
		cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		cursor.maxTime(7, TimeUnit.DAYS);
		for (DBObject proxyInfo : cursor) {
			browser.setProxy((String) proxyInfo.get("IPv4"), (String) proxyInfo.get("port"));
			BasicDBList tag = new BasicDBList();
			BasicDBList delay = new BasicDBList();
			for (String key : checkSubject.keySet()) {
				long beginTime = System.currentTimeMillis();
				BasicHttpResponse r = browser.get(checkSubject.get(key));
				if (r == null) {
					continue;
				}
				long endTime = System.currentTimeMillis();
				tag.add(key);
				delay.add(endTime - beginTime);
			}
			if (tag.size() == 0) {
				System.out.println(proxyInfo.get("_id") + " 没什么用");
				collection.remove(proxyInfo);
			} else {
			//	DBObject newProxyInfo = new BasicDBObject(proxyInfo.toMap());
				proxyInfo.put("tag", tag);
				proxyInfo.put("delay", delay);
				collection.save(proxyInfo);
			}
		}
		cursor.close();
		browser.close();
	}

	public static void main(String[] args) {
		init();
	//	GrabYoudaili();
		checkProxyPool();
		exit();
	}
}
