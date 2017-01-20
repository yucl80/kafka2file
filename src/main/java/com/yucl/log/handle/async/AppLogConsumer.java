/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yucl.log.handle.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.FileSystem;
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

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class AppLogConsumer extends Thread {
	private final ConsumerConnector consumer;
	private final String topic;
	private static ConcurrentHashMap<String, ChannelWrapper> channels = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(AppLogConsumer.class);

	public AppLogConsumer(String topic) {
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
		ChannelCloseThread channelCloseThread = new ChannelCloseThread(channels);
		channelCloseThread.start();
		FileSystem fileSystem = FileSystems.getDefault();
		String osName = System.getProperty("os.name");
		String rootDir = "";
		if (osName.indexOf("Window") > -1) {
			rootDir = "c:";
		}
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			String msg = new String(it.next().message());
			if (logger.isDebugEnabled()) {
				logger.debug(msg);
			}
			try {
				DocumentContext jsonContext = JsonPath.parse(msg);
				String rawMsg = jsonContext.read("$.message", String.class);
				String date = rawMsg.substring(0, 10);
				String rawPath = jsonContext.read("$.path", String.class);
				String filePath = new StringBuilder().append(rootDir).append("/mwbase/applogs/rtlog/")
						.append(jsonContext.read("$.stack", String.class)).append("-")
						.append(jsonContext.read("$.service", String.class)).append("/").append(date).append(".")
						.append(rawPath.substring(rawPath.lastIndexOf('/') + 1)).append(".")
						.append((String) jsonContext.read("$.index", String.class)).toString();
				Path path = fileSystem.getPath("", filePath);
				ChannelWrapper channelWrapper = channels.get(path.toString());

				if (channelWrapper == null) {
					synchronized (channels) {
						channelWrapper = channels.get(path);
						if (channelWrapper == null) {
							if (!Files.exists(path.getParent())) {
								Files.createDirectories(path.getParent());
							}
							AsynchronousFileChannel asyncFile = AsynchronousFileChannel.open(path,
									StandardOpenOption.WRITE, StandardOpenOption.CREATE);
							channelWrapper = new ChannelWrapper(asyncFile, asyncFile.size());
							channels.put(path.toString(), channelWrapper);
						}
					}
				}
				synchronized (channelWrapper) {
					byte[] bytes = (rawMsg + "\n").getBytes();
					channelWrapper.getFileChannel().write(ByteBuffer.wrap(bytes), channelWrapper.getPos());
					channelWrapper.setPos(channelWrapper.getPos() + bytes.length);
					channelWrapper.setLastWriteTime(System.currentTimeMillis());
				}

			} catch (IOException e) {
				logger.error(msg, e);
			}

		}

	}
}
