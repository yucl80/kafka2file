
package com.yucl.log.handle.async;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class LogConsumer extends Thread {
	private final Logger logger = LoggerFactory.getLogger(getClass());;
	private final ConsumerConnector consumer;
	private final String topic;
	private ConcurrentHashMap<String, ChannelWrapper> channels = new ConcurrentHashMap<>();
	private ThreadPoolExecutor threadPoolExecutor;

	public LogConsumer(String topic) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;		
	}

	public LogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
		this.threadPoolExecutor = threadPoolExecutor;		
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

	public abstract String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir);

	public void run() {
		ChannelCloseThread channelCloseThread = new ChannelCloseThread(channels);
		channelCloseThread.start();
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
				String filePath = buildFilePathFromMsg(jsonContext, rootDir);
				Path path = FileSystems.getDefault().getPath("", filePath);
				String rawMsg = jsonContext.read("$.message");
				ChannelWrapper channelWrapper = channels.get(path.toString());
				if (channelWrapper == null) {
					synchronized (channels) {
						channelWrapper = channels.get(path);
						if (channelWrapper == null) {
							if (!Files.exists(path.getParent())) {
								Files.createDirectories(path.getParent());
							}
							long fileLength = new File(path.toString()).length();
							AsynchronousFileChannel asyncFile = null;
							if (threadPoolExecutor != null) {
								asyncFile = AsynchronousFileChannel.open(path,
										new HashSet<StandardOpenOption>(
												Arrays.asList(StandardOpenOption.WRITE, StandardOpenOption.CREATE)),
										threadPoolExecutor);
							} else {
								asyncFile = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE,
										StandardOpenOption.CREATE);
							}
							channelWrapper = new ChannelWrapper(asyncFile, fileLength);
							channels.put(path.toString(), channelWrapper);
						}
					}
				}
				synchronized (channelWrapper) {
					byte[] bytes =getBytesToWrite(rawMsg);
					channelWrapper.getFileChannel().write(ByteBuffer.wrap(bytes), channelWrapper.getPos());
					channelWrapper.setPos(channelWrapper.getPos() + bytes.length);
					channelWrapper.setLastWriteTime(System.currentTimeMillis());
				}
			} catch (IOException e) {
				logger.error(msg, e);
			} catch (InvalidPathException e){
				logger.error(msg, e);
			} catch (Throwable t){
				logger.error(msg, t);
			}

		}

	}

	public byte[] getBytesToWrite(String rawMsg){
		return  (rawMsg + "\n").getBytes();
	}
}
