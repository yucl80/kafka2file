package com.learn.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.Partitioner;
import kafka.producer.ProducerConfig;

public class SimpleProducer {
	private static Producer<Integer, String> producer;
	
	private static final Properties props = new Properties();

	public SimpleProducer()
	{
		props.put("metadata.broker.list", "192.168.8.145:9092,192.168.8.146:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		//props.put("partitioner.class", "com.learn.kafka.SimplePartitioner");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props); 
		producer = new Producer<Integer, String>(config);
	}

	public static void main(String[] args) {
		SimpleProducer sp = new SimpleProducer();
		final String topic = "test5";
		final String messageStr ="this is a test";
		long l=System.currentTimeMillis();
		Thread t1=new Thread(){
			public void run(){
				for(int i=0;i<1000;i++){
				KeyedMessage<Integer, String> data = new KeyedMessage<Integer,String>(topic,messageStr);
				
				producer.send(data);
				}
			}
		};
		props.put("metadata.broker.list", "192.168.8.147:9092");
		producer = new Producer<Integer, String>(new
				ProducerConfig(props));
		Thread t2=new Thread(){
			public void run(){
				for(int i=0;i<1000;i++){
				KeyedMessage<Integer, String> data = new KeyedMessage<Integer,
						String>(topic, messageStr);
				producer.send(data);
				}
			}
		};
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(System.currentTimeMillis()-l);
		producer.close();
	}
}
