package com.atmosphere.controller;

import javax.inject.Inject;

import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atmosphere.coders.JacksonDecoder;
import com.atmosphere.coders.JacksonEncoder;

@ManagedService(path = "/test/message")
public class AtmosphereServerController {
	
	@Inject
	private AtmosphereResource atmosphereResource;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Ready
	public void onReady(){
		this.logger.info("Connected {} ", atmosphereResource.uuid());
	}
	
	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		System.out.println("Got disconnected >>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
	
	@org.atmosphere.config.service.Message(decoders = {JacksonDecoder.class} , encoders = {JacksonEncoder.class})
	public Message onMessage(Message message){
		return  message;
	}
}
