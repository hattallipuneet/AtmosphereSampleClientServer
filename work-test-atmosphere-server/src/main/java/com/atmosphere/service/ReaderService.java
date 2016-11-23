package com.atmosphere.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atmosphere.controller.Message;
import com.atmosphere.notifier.BroadcastNotifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReaderService {
	
	private static ExecutorService readerService;
	
	private static volatile boolean running = false;
	
	private ObjectMapper mapper;
	
	@Autowired
	private BroadcastNotifier notifier;
	
	@PostConstruct
	public void init() throws Exception{
		System.out.println("After Bean Creation");
		running = true;
		mapper = new ObjectMapper();
		readerService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				 t.setDaemon(true);
				 t.setName("Reader Thread");
				return t;
			}
		});
		generateMessages();
	}
	
	private void generateMessages(){
		if(running){
			System.out.println("Starting to generate message");
			readerService.submit(new ReaderRunnable());
		}
		
		if(!running){
			try {
				System.out.println("Thread is not running");
				readerService.awaitTermination(TimeUnit.MILLISECONDS.toMillis(1000), TimeUnit.MILLISECONDS);
				readerService.shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private class ReaderRunnable implements Runnable{
		
		int noOfMessagesGenerated = 0;
		
		public void run() {
			System.out.println("Thread Started");
			String message = "";
			while(running){
				System.out.println("Thread is running");
				System.out.println("Generated Message Count "+noOfMessagesGenerated);
				if(noOfMessagesGenerated < 10){
					try {
						noOfMessagesGenerated++;
						message = mapper.writeValueAsString(new Message("This is message number "+noOfMessagesGenerated));
						notifier.notifyMessage(message);
						Thread.sleep(5000);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
				}else{
					running = false;
					try {
						message = mapper.writeValueAsString(new Message("EOF"+noOfMessagesGenerated));
						notifier.notifyMessage(message);
						System.out.println("This is else path");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
