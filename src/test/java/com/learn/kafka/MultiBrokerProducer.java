package com.learn.kafka;

import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class MultiBrokerProducer {
	private static Producer<String, String> producer1;
	private static Producer<String, String> producer2;
	private static Producer<String, String> producer3;
	private static final Properties props = new Properties();
    private static String msg;
	static
	{
		props.put("metadata.broker.list", "192.168.8.145:9092,192.168.8.146:9092,192.168.8.147:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		//props.put("partitioner.class", "test.kafka.SimplePartitioner");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		producer1 = new Producer<String, String>(config);
		producer2 = new Producer<String, String>(config);
		producer3 = new Producer<String, String>(config);
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<1024;i++){
			sb.append("a");
		}
		msg = sb.toString();
	}

	public static void main(String[] args) {
		long l=System.currentTimeMillis();
		Thread t1=new Thread(){
			public void run(){
				send(producer1);
			}
		};
		Thread t2=new Thread(){
			public void run(){
				send(producer2);
			}
		};
		Thread t3=new Thread(){
			public void run(){
				send(producer3);
			}
		};
		t1.start();
		t2.start();
		t3.start();
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis()-l);
		
	}

	private static void send(Producer<String, String> producer) {
		MultiBrokerProducer sp = new MultiBrokerProducer();
		Random rnd = new Random();
		String topic = "test";
		
		for (long messCount = 0; messCount < 10000; messCount++) {
			Integer key = rnd.nextInt(255);
			//String msg = msg;
			KeyedMessage<String, String> data1 = new
					KeyedMessage<String, String>(topic, key.toString(), msg);
			producer.send(data1);
		}
		
		producer.close();
	}
	

	
}