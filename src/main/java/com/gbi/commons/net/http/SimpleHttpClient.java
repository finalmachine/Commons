package com.gbi.commons.net.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class SimpleHttpClient implements Closeable {

	// default config
	private static final int defaultConnectTimeout = 240000;
	private static final int defaultSocketTimeout = 240000;
	private static final int setConnectionRequestTimeout = 200000;
	private static final int defaultMaxRedirects = 5;
	
	// default headers

	// default error code
	private static final int ConnectTimeoutError = -1;
	private static final int SocketTimeoutError = -2;
	private static final int OverMaxRedirectsError = -3;
	private static final int HttpHostConnectError = -4;
	private static final int SocketExceptionError = -5; // 有可能是服务器忽然被关闭了
	private static final int NoHttpResponseError = -6;
	private static final int SSLHandshakeError = -7;

	private CloseableHttpClient client = null;
	private HttpClientContext context = null;
	private HttpRequestBase request = null;
	private CloseableHttpResponse response = null;
	private RequestConfig config = null;
	private HttpHost proxy = null;
	private int LastStatus = 0;

	/**
	 * 建立一个简单的
	 */
	public SimpleHttpClient() {
		// 将HTTPS的网站证书设置成不检查的状态
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("TLSv1");
			sslcontext.init(null, new TrustManager[] { truseAllManager }, null);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
	//	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);//new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1", "SSLv3" },
				null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		// 初始化context
		context = new HttpClientContext();
	}
	
	/**
	 * @param requestBase the HTTP request to be send
	 * Accept			text/html, application/xhtml+xml, 
	 * 					application/xml;q=0.9, application/json;q=0.9,
	 * 					image/webp,
	 * 					other;q=0.8
	 * Accept-Encoding*	gzip, deflate
	 * Accept-Language	zh-CN,zh;q=0.8
	 * Connection*		keep-alive
	 * Host*			as you need
	 * User-Agent		Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36
	 */
	private void setHeaders(HttpRequestBase requestBase) {
		requestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,application/json;q=0.9,image/webp,*/*;q=0.8");
		requestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		requestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
	}
	
	private void prepare(HttpMethod method, String uri, Map<String, String> data) {
		switch (method) {
		case GET:
			HttpGet requestGet = new HttpGet(uri);
			setHeaders(requestGet);
			request = requestGet;
			break;
		case POST:
			HttpPost requestPost = new HttpPost(uri);
			List<NameValuePair> list = new ArrayList<>();
			for (String key : data.keySet()) {
				list.add(new BasicNameValuePair(key, data.get(key)));
			}
			try {
				requestPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			request = requestPost;
			break;
		default:
			break;
		}
		LastStatus = 0;
		// 设置http请求的配置参数
		config = RequestConfig.custom()//
				.setMaxRedirects(defaultMaxRedirects)//
				.setSocketTimeout(defaultSocketTimeout)//
				.setConnectTimeout(defaultConnectTimeout)//
				.setConnectionRequestTimeout(setConnectionRequestTimeout)//
				.setProxy(proxy)//
				.build();
		request.setConfig(config);
	}

	public SimpleHttpResponse get(final String uri) {
		return get(uri, true);
	}

	public SimpleHttpResponse get(final String uri, boolean onlySucessfulEntity) {
		prepare(HttpMethod.GET, uri, null);
		try {
			response = client.execute(request, context);
			LastStatus = response.getStatusLine().getStatusCode();
			if (onlySucessfulEntity) {
				if (LastStatus / 100 != 2) {
					return null;
				}
			}
			SimpleHttpResponse toReturn = new SimpleHttpResponse(context);
			response.close();
			return toReturn;
		} catch (ConnectTimeoutException e) {
			LastStatus = ConnectTimeoutError;
		} catch (SocketTimeoutException e) {
			LastStatus = SocketTimeoutError;
		} catch (HttpHostConnectException e) {
			LastStatus = HttpHostConnectError;
		} catch (ClientProtocolException e) {
			if (e.getCause() instanceof RedirectException) {
				LastStatus = OverMaxRedirectsError;
			} else {
				System.err.println("throw");
				throw new RuntimeException(e);
			}
		} catch (SocketException e) {
			LastStatus = SocketExceptionError;
		} catch (NoHttpResponseException e) {
			LastStatus = NoHttpResponseError;
		} catch (SSLHandshakeException e) {
			LastStatus = SSLHandshakeError;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}
	
	public SimpleHttpResponse post(final String uri, Map<String, String> data) {
		return post(uri, data, true);
	}

	public SimpleHttpResponse post(final String uri, Map<String, String> data, boolean onlySucessfulEntity) {
		prepare(HttpMethod.POST, uri, data);
		try {
			response = client.execute(request, context);
			LastStatus = response.getStatusLine().getStatusCode();
			if (LastStatus == 302) {
				String url = response.getFirstHeader("Location").getValue();
				response.close();
				return get(url, onlySucessfulEntity);
			} else {
				if (onlySucessfulEntity) {
					if (LastStatus / 100 != 2) {
						response.close();
						return null;
					}
				}
			}
			SimpleHttpResponse toReturn = new SimpleHttpResponse(context);
			response.close();
			return toReturn;
		} catch (ConnectTimeoutException e) {
			LastStatus = ConnectTimeoutError;
		} catch (SocketTimeoutException e) {
			LastStatus = SocketTimeoutError;
		} catch (HttpHostConnectException e) {
			LastStatus = HttpHostConnectError;
		} catch (ClientProtocolException e) {
			if (e.getCause() instanceof RedirectException) {
				LastStatus = OverMaxRedirectsError;
			} else {
				System.err.println("throw");
				throw new RuntimeException(e);
			}
		} catch (SocketException e) {
			LastStatus = SocketExceptionError;
		} catch (NoHttpResponseException e) {
			LastStatus = NoHttpResponseError;
		} catch (SSLHandshakeException e) {
			LastStatus = SSLHandshakeError;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}
	
	public CookieStore getCookieStore() {
		return context.getCookieStore();
	}

	/**
	 * 得到最后得到的HTTP状态码
	 * 
	 * @return = -3} 超过最大重定向次数 = -2} Socket连接超时 = -1} Connect连接超时 = 0} 未设置 >= 1}
	 *         HTTP协议规定的返回码
	 */
	public int getLastStatus() {
		return LastStatus;
	}
	
    /**
     * Set proxy of the HttpClient by hostname and port.
     *
     * @param hostname  the hostname (IP or DNS name)
     * @param port      the port number.
     *                  {@code -1} indicates the scheme default port.
     */
	public void setProxy(String hostname, int port) {
		proxy = new HttpHost(hostname, port);
	}

	/**
	 * 重写验证方法，取消检测ssl
	 */
	private static TrustManager truseAllManager = new X509TrustManager() {
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	@Override
	public void close() {
		try {
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			System.err.println("SimpleHttpClient close error");
		}
	}

	public static void main(String[] args) throws Exception {
		SimpleHttpClient client = new SimpleHttpClient();
		Map<String, String> data = new HashMap<String, String>();
		data.put("p1", "123");
		data.put("p2", "abc");
		SimpleHttpResponse response = client.post(
				"http://localhost:8080/WebTest/servlet/test", data, false);
		if(client.getLastStatus() / 100 == 2) {
			System.out.println(response.getUrl());
			System.out.println(response.getContentType());
			System.out.println(response.getContentCharset());
			System.out.println(response.getContent().length);
		} else {
			System.out.println("code:" + client.getLastStatus());
		}
		client.close();
	}
}
