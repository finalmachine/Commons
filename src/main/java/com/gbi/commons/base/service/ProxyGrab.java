package com.gbi.commons.base.service;

import com.gbi.commons.net.http.BasicHttpClient;
import com.gbi.commons.net.http.BasicHttpResponse;

public class ProxyGrab {
	public static void main(String[] args) {
		BasicHttpClient client = new BasicHttpClient();
	//	client.setProxy("113.53.230.154",3129);
		BasicHttpResponse response = client.get("http://my.oschina.net/fhd/blog/344961");//  http://www.youdaili.net/Daili/guowai/
		if (client.getLastStatus() != 200) {
			System.out.println(client.getLastStatus());
		} else {
			System.out.println(response.getContentCharset());
			System.out.println(response.getDocument());
		}
		client.close();
	}
}
