package com.atmosphere.controller;

public class Message {
	String value;

	public Message() {}
	
	public Message(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Message [value=" + value + "]";
	}
	
}
