package com.atmosphere.test.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Options;
import org.atmosphere.wasync.Request.METHOD;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.Socket.STATUS;
import org.atmosphere.wasync.impl.AtmosphereClient;
import org.atmosphere.wasync.impl.DefaultOptions;
import org.atmosphere.wasync.impl.DefaultOptionsBuilder;

import com.atmosphere.controller.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProviderConfig;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;

public class TestClient {

	private static AtmosphereClient getClient(){
		return ClientFactory.getDefault().newClient(AtmosphereClient.class);
	}

	public static void main(String[] args) {

		try {
			Socket socket = null;
			System.out.println("Starting client");

			socket = tryToConnect();
			
			String a = ""; String name = null;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));){
				while (!(a.equals("quit"))) {
					a = br.readLine();
					if (name == null) {
						name = a;
					}
					socket.fire(new Message(name));
				}
			} catch (Exception e) {
				
			} finally {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().exit(0);
	}

	static void getSleep(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private  static AsyncHttpClient createDefaultAsyncHttpClient(int requestTimeoutInSeconds, AsyncHttpProviderConfig asyncHttpProviderConfig) {
		AsyncHttpClientConfig.Builder b = new AsyncHttpClientConfig.Builder();
		b.setFollowRedirect(true).setConnectTimeout(-1)
		.setReadTimeout(requestTimeoutInSeconds == -1 ? requestTimeoutInSeconds : requestTimeoutInSeconds * 1000)
		.setUserAgent("wAsync/2.0");
		b.setRequestTimeout(-1);
		b.setAcceptAnyCertificate(Boolean.TRUE);
		AsyncHttpClientConfig config = b.setAsyncHttpClientProviderConfig(asyncHttpProviderConfig).build();
		return new AsyncHttpClient(config);
	}

	private static AsyncHttpClient createDefaultAsyncHttpClient(Options o) {
		NettyAsyncHttpProviderConfig nettyConfig = new NettyAsyncHttpProviderConfig();
		nettyConfig.addProperty("child.tcpNoDelay", "true");
		nettyConfig.addProperty("child.keepAlive", "true");
		return createDefaultAsyncHttpClient(o.requestTimeoutInSeconds(), nettyConfig);  
	}

	static Socket tryToConnect() throws Exception{
		final ObjectMapper mapper = new ObjectMapper();
		DefaultOptionsBuilder optionsBuilder = getClient().newOptionsBuilder().reconnect(true);
		AsyncHttpClient asyncHttpClient = createDefaultAsyncHttpClient(optionsBuilder.build());
		DefaultOptions options = getClient().newOptionsBuilder().runtime(asyncHttpClient).runtimeShared(true).build();
		Socket socket = null;
		boolean connected = false;
		int noOfRetry = 5;
		while(!connected && noOfRetry > 0){
			System.out.println("Count Value of Retry "+noOfRetry);
			try {
				noOfRetry --;
				socket = getClient().create(options);
				socket.on(Event.OPEN, new Function<String>() {
					public void on(String msg) {
						System.out.println(msg);					
					}
				}).on("message", new Function<String>() {
					public void on(String msg) {
						System.out.println(msg);				
					}
				}).on(new Function<Throwable>() {
					public void on(Throwable msg) {
						System.out.println(msg);					
					}
				}).on(Event.CLOSE, new Function<String>() {
					public void on(String msg) {
						System.out.println(msg);					
					}
				}).on(Event.REOPENED, new Function<String>() {
					public void on(String msg) {
						System.out.println(msg);
					}
				}).on(Event.REOPENED, new Function<String>() {
					public void on(String msg) {
						System.out.println(msg);					
					}
				})
				.open(requestBuilder(mapper).build());
				connected = true;
			} catch (Exception e) {
				if(socket != null && socket.status() != STATUS.CLOSE){
					socket.close();
				}
				connected = false;
				System.out.println("Count Value of Retry "+noOfRetry);
			}
		}
		if(noOfRetry == 0){
			System.out.println("Count Value of Retry "+noOfRetry);
			if(socket != null && socket.status() != STATUS.CLOSE){
				socket.close();
			}
			throw new RuntimeException("Server is down");
		}
		return  socket;
	}


	@SuppressWarnings("rawtypes")
	static RequestBuilder requestBuilder(final ObjectMapper mapper){
		RequestBuilder requestBuilder = getClient().newRequestBuilder()
				.decoder(new Decoder<String, Message>(){
					public Message decode(Event event, String msg) {
						try {
							return mapper.readValue(msg, Message.class);
						} catch (Exception e) {
							throw new RuntimeException();
						} 
					}
				}).encoder(new Encoder<Message, String>() {
					public String encode(Message msg) {
						try {
							return mapper.writeValueAsString(msg);
						} catch (Exception e) {
							throw new RuntimeException();
						}
					}
				}).method(METHOD.GET)
				.uri("http://localhost:8080/worktest-atmosphere-server/test/message")
				.trackMessageLength(true);
		return requestBuilder;
	}

}
