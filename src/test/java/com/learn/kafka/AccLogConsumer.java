
package com.learn.kafka;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;
import com.yucl.log.handle.sync.KafkaProperties;
import com.yucl.log.handle.sync.StreamCloseThread;
import com.yucl.log.handle.sync.StreamHandle;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class AccLogConsumer extends Thread {
	private final ConsumerConnector consumer;
	private final String topic;
	private static ConcurrentHashMap<String, StreamHandle> outputStreams = new ConcurrentHashMap<>();	
	private Logger logger = LoggerFactory.getLogger(AccLogConsumer.class);
	
	public AccLogConsumer(String topic) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", KafkaProperties.zkConnect);
		props.put("group.id", KafkaProperties.groupId);
		props.put("zookeeper.session.timeout.ms", "4000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");

		return new ConsumerConfig(props);

	}

	public void run() {
		StreamCloseThread StreamCloseThread = new StreamCloseThread(outputStreams);
		StreamCloseThread.start();
		String osName = System.getProperty("os.name");
		String rootDir = "";
		if(osName.indexOf("Window")> -1){
			rootDir = "c:";
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			String msg = new String(it.next().message());
			if(logger.isDebugEnabled()){
				logger.debug(msg);
			}
			Path path = FileSystems.getDefault().getPath("", rootDir + JsonPath.read(msg, "$.path"));			
			String rawMsg = JsonPath.read(msg, "$.message");
			StreamHandle streamHandle = outputStreams.get(path);
			try {				
				if (streamHandle == null) {
					if (!Files.exists(path.getParent())) {
						Files.createDirectories(path.getParent());
					}
					OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
					streamHandle = new StreamHandle(out);
					outputStreams.put(path.toString(), streamHandle);
				}
				streamHandle.getOutputStream().write((rawMsg + "\n").getBytes());
				streamHandle.setLastWriteTime(System.currentTimeMillis());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
