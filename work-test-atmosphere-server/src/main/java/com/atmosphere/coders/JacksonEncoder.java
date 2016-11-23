package com.atmosphere.coders;

import org.atmosphere.config.managed.Encoder;

import com.atmosphere.controller.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonEncoder implements Encoder<String, Message> {
	
	private final static ObjectMapper mapper = new ObjectMapper();
	
	public Message encode(String stringMessage) {
		try {
			System.out.println("Input Message "+stringMessage);
			return mapper.readValue(stringMessage, Message.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
