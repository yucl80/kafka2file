package com.yucl.log.handle.async;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCloseThread extends Thread {

	private ConcurrentHashMap<String, ChannelWrapper> channels;
	private Logger logger = LoggerFactory.getLogger(ChannelCloseThread.class);

	public ChannelCloseThread(ConcurrentHashMap<String, ChannelWrapper> channels) {
		this.channels = channels;
		this.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("close all channel :"+channels.size());
				for (Entry<String, ChannelWrapper> entry : channels.entrySet()) {					
					try {
						entry.getValue().getFileChannel().force(false);
						entry.getValue().getFileChannel().close();						
					} catch (IOException e) {
						logger.error(e.getMessage(),e);
					}

				}

			}
		});
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(300000);
				logger.info("total channel count:"+channels.size() );
				for (Entry<String, ChannelWrapper> entry : channels.entrySet()) {
					if (System.currentTimeMillis() - entry.getValue().getLastWriteTime() > 300000) {
						logger.info("close channel "+ entry.getKey());
						channels.remove(entry.getKey());
						entry.getValue().getFileChannel().force(false);
						entry.getValue().getFileChannel().close();						
					}
				}

			} catch (InterruptedException | IOException e) {
				logger.error(e.getMessage(),e);
			}

		}
	}

}
