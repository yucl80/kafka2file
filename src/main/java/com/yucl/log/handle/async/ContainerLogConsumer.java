package com.yucl.log.handle.async;

import java.util.concurrent.ThreadPoolExecutor;

import com.jayway.jsonpath.DocumentContext;

public class ContainerLogConsumer extends LogConsumer {
	
	public ContainerLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);		
	}

	public ContainerLogConsumer(String topic) {
		super(topic);		
	}

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {
		String filePath = new StringBuilder().append(rootDir)
				.append("/mwbase/applogs/rtlog/")
				.append(msgJsonContext.read("$.stack", String.class)).append("-")
		        .append(msgJsonContext.read("$.service", String.class)).append("/")
		        .append("console.out")
		        .append(".").append(msgJsonContext.read("$.index", String.class))
		        .toString();
		return filePath;
	}
}
