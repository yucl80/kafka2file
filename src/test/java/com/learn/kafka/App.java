package com.learn.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Properties parameters=new Properties();
		ProducerConfig config = new ProducerConfig(parameters);
    	Producer<String, String> producer= new Producer<String, String>(config);
        System.out.println( "Hello World!" );
    }
}
