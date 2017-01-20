package com.yucl.log.handle.async;

import java.util.concurrent.ThreadPoolExecutor;

import com.jayway.jsonpath.DocumentContext;

public class AppLogConsumer extends LogConsumer {
	public AppLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);
	}

	public AppLogConsumer(String topic) {
		super(topic);

	}

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {
		String rawMsg = msgJsonContext.read("$.message", String.class);
		String date = rawMsg.substring(0, 10);
		String rawPath = msgJsonContext.read("$.path", String.class);
		String filePath = new StringBuilder().append(rootDir).append("/mwbase/applogs/rtlog/")
				.append(msgJsonContext.read("$.stack", String.class)).append("-")
				.append(msgJsonContext.read("$.service", String.class)).append("/").append(date).append(".")
				.append(rawPath.substring(rawPath.lastIndexOf('/') + 1)).append(".")
				.append((String) msgJsonContext.read("$.index", String.class)).toString();
		return filePath;
	}
}
