package com.yucl.log.handle.async;

import com.jayway.jsonpath.DocumentContext;

import java.util.Calendar;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

public class AppLogConsumer extends LogConsumer {
    Pattern pattern = Pattern.compile("^\\[\\d{2}/\\d{2} ");

	public AppLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);
	}

	public AppLogConsumer(String topic) {
		super(topic);

	}

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {
		String rawMsg = msgJsonContext.read("$.message", String.class);
		String date;
		if (pattern.matcher(rawMsg).find()){
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String eventTime = year+"-"+rawMsg.substring(1,6);
            date = eventTime.replaceAll("/","-");
		}else {
            date = rawMsg.substring(0, 10);
		}
		String rawPath = msgJsonContext.read("$.path", String.class);
		String filePath = new StringBuilder().append(rootDir).append("/app/logs/")
				.append(msgJsonContext.read("$.stack", String.class)).append("/")
				.append(msgJsonContext.read("$.service", String.class)).append("/").append(date).append(".")
				.append(rawPath.substring(rawPath.lastIndexOf('/') + 1)).append(".")
				.append((String) msgJsonContext.read("$.index", String.class)).toString();
		return filePath;
	}
}
