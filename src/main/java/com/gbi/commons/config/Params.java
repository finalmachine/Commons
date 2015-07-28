package com.gbi.commons.config;

public class Params {
	public enum MongoDB {
		PROXIES("127.0.0.1", 27017, "PROXIES");
		
		public String host;
		public int port;
		public String database;
		
		MongoDB(String host, int port, String database) {
			this.host = host;
			this.port = port;
			this.database = database;
		}
	}
}
