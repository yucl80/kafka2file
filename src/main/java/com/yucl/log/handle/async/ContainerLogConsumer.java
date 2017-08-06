package com.yucl.log.handle.async;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadPoolExecutor;

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
				.append("/applogs/rtlog/")
				.append(msgJsonContext.read("$.stack", String.class)).append("/")
		        .append(msgJsonContext.read("$.service", String.class)).append("/")
		        .append("console.out")
		        .append(".").append(msgJsonContext.read("$.index", String.class))
		        .toString();
		return filePath;
	}

	@Override
	public byte[] getBytesToWrite(String rawMsg) {
		DocumentContext jsonContext = JsonPath.parse(rawMsg);
		String log = jsonContext.read("$.log",String.class);
		byte[] bytes = new byte[0];
		try {
			bytes =log.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		return bytes;
	}
}
