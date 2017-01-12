package com.yucl.log.handle.sync;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamCloseThread extends Thread {

	private ConcurrentHashMap<String, StreamHandle> outputStreams;
	private Logger logger = LoggerFactory.getLogger(StreamCloseThread.class);

	public StreamCloseThread(ConcurrentHashMap<String, StreamHandle> outputStreams) {
		this.outputStreams = outputStreams;
		this.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("close all OutputSteam");
				for (Entry<String, StreamHandle> entry : outputStreams.entrySet()) {					
					try {
						entry.getValue().getOutputStream().flush();
						entry.getValue().getOutputStream().close();
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
				for (Entry<String, StreamHandle> entry : outputStreams.entrySet()) {
					if (System.currentTimeMillis() - entry.getValue().getLastWriteTime() > 300000) {
						logger.info("close outputStream "+ entry.getKey());
						outputStreams.remove(entry.getKey());
						entry.getValue().getOutputStream().flush();
						entry.getValue().getOutputStream().close();
					}
				}

			} catch (InterruptedException | IOException e) {
				logger.error(e.getMessage(),e);
			}

		}
	}

}
