package com.atmosphere.notifier;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterListenerAdapter;
import org.atmosphere.cpr.Deliver;
import org.atmosphere.cpr.Universe;
import org.springframework.stereotype.Component;

import com.atmosphere.controller.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BroadcastNotifier {
	
	private static BroadcasterFactory factory = null;
	private static ObjectMapper mapper = null;
	static {
		mapper = new ObjectMapper();
		factory = Universe.broadcasterFactory();
		factory.addBroadcasterListener(new BroadcasterListenerAdapter(){
			@Override
			public void onMessage(Broadcaster b, Deliver deliver) {
				System.out.println(deliver.getMessage()+ "  TTTTT  "+ deliver.getOriginalMessage());
			}
		});
	}
	
	public void notifyMessage(String messageString){
		System.out.println("Message being Sent "+messageString);
		if((factory = Universe.broadcasterFactory()) != null){
			try {
				Message message = mapper.readValue(messageString, Message.class);
				System.out.println("");
				factory.lookup("/test/message").broadcast(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Factory is null");
		}
	}
	
}
