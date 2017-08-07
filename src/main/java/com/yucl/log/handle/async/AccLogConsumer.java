
package com.yucl.log.handle.async;

import com.jayway.jsonpath.DocumentContext;

import java.util.concurrent.ThreadPoolExecutor;

public class AccLogConsumer extends LogConsumer {

	public AccLogConsumer(String topic) {
		super(topic);		
	}

	public AccLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);		
	}

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {
		String rawPath = msgJsonContext.read("$.path", String.class);

		String filePath = new StringBuilder().append(rootDir).append("/app/logs/")
				.append(msgJsonContext.read("$.stack", String.class)).append("/")
				.append(msgJsonContext.read("$.service", String.class)).append("/")
				.append(msgJsonContext.read("$.service", String.class)).append("-")
				.append(msgJsonContext.read("$.index", String.class)).append(".")
				.append(rawPath.substring(rawPath.lastIndexOf('/') + 1))
				.toString();
		return filePath;
	}
	
	
}
