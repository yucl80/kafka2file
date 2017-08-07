package com.yucl.log.handle.async;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContainerLogConsumer extends LogConsumer {
	
	public ContainerLogConsumer(String topic, ThreadPoolExecutor threadPoolExecutor) {
		super(topic, threadPoolExecutor);		
	}

	public ContainerLogConsumer(String topic) {
		super(topic);		
	}

    Pattern dockerLogTimePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})");

	@Override
	public String buildFilePathFromMsg(DocumentContext msgJsonContext, String rootDir) {
        String filePath ;
        String rawMsg = msgJsonContext.read("$.message",String.class);
        DocumentContext jsonContext = JsonPath.parse(rawMsg);
        String time = jsonContext.read("$.time", String.class);
        Matcher matcher = dockerLogTimePattern.matcher(time);
        Date date = null;
        if(matcher.find()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");
            try {
                 date = sdf.parse(matcher.group(1)+" UTC");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if ( date == null){
            date = new Date();
        }
	    if(! msgJsonContext.read("$.stack", String.class).isEmpty() ){
             filePath = new StringBuilder().append(rootDir).append("/app/logs/")
                    .append(msgJsonContext.read("$.stack", String.class)).append("/")
                    .append(msgJsonContext.read("$.service", String.class)).append("/")
                    .append(msgJsonContext.read("$.service", String.class)).append("-")
                    .append(msgJsonContext.read("$.index", String.class)).append(".")
                    .append("console.out").append(".")
                    .append(new SimpleDateFormat("yyyy-MM-dd").format(date))
                    .toString();
        } else {
	        String containerName = msgJsonContext.read("$.Name", String.class);
            filePath = new StringBuilder().append(rootDir).append("/app/logs/")
                    .append("others/")
                    .append(containerName)
                    .append(".").append(new SimpleDateFormat("yyyy-MM-dd").format(date))
                    .append(".out")
                    .toString();
        }

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
