package com.atmosphere.coders;

import org.atmosphere.config.managed.Decoder;

import com.atmosphere.controller.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDecoder implements Decoder<Message, String> {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public String decode(Message message) {
		try {
			System.out.println("Message to be decoded is "+message);
			return MAPPER.writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
