
package com.yucl.log.handle.async;

import java.util.concurrent.ThreadPoolExecutor;

import com.jayway.jsonpath.DocumentContext;

public class SysLogConsumer extends LogConsumer {	
	
	public SysLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);		
	}

	public SysLogConsumer(String topic) {
		super(topic);
	}

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {		
		return rootDir + msgJsonContext.read("$.path", String.class);
	}
}
