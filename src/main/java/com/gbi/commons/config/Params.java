package com.gbi.commons.config;

public class Params {
	public enum MongoDB {
		NAVISUS("127.0.0.1", 27017, "NAVISUS"),
		PROXIES("127.0.0.1", 27017, "PROXIES"),
		TEST("127.0.0.1", 27017, "TEST");
		public String host;
		public int port;
		public String database;
		
		MongoDB(String host, int port, String database) {
			this.host = host;
			this.port = port;
			this.database = database;
		}
	}
	
	public static final class StableProxy {
		public static String host = "192.168.0.116";
		public static int port = 1080;
	}
}
