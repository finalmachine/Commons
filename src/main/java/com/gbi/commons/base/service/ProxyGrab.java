package com.gbi.commons.base.service;

import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.gbi.commons.config.Params;
import com.gbi.commons.net.http.BasicHttpClient;
import com.gbi.commons.net.http.BasicHttpResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class ProxyGrab {
	
	private static MongoClient client = null;
	static {
		try {
			client = new MongoClient(Params.MongoDB.PROXIES.host, Params.MongoDB.PROXIES.port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	// 抓取有代理的代理服务器地址
	public static void GrabYoudaili() {
		BasicHttpClient browser = new BasicHttpClient();
		BasicHttpResponse response = browser.get("http://www.youdaili.net/Daili/guowai/");
		if (response == null) {
			browser.close();
			throw new RuntimeException("有代理访问失败");
		}
		// 抓取首页 >
		DBCollection collection = client.getDB(Params.MongoDB.PROXIES.database).getCollection("proxies");
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
					System.out.println("------------");
				}
			}
			break;// TODO
		}
		// 抓取首页 <
		browser.close();
	}

	public static void main(String[] args) {
	//	GrabYoudaili();
		DB d1 = client.getDB(Params.MongoDB.PROXIES.database);
		DB d2 = client.getDB(Params.MongoDB.PROXIES.database);
		DBCollection c1 = d1.getCollection("proxies");
		DBCollection c2 = d2.getCollection("proxies");
		System.out.println(d1 == d2);
		System.out.println(c1 == c2);
	}
}
