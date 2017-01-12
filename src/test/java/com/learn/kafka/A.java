package com.learn.kafka;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class A {

	public static void main(String[] args) {
		for(Path path :FileSystems.getDefault().getRootDirectories()) {
			System.out.println(path.toString());
		}
		String rawMsg="2017-01-10 10:31:26,447  INFO  http-apr-8080-exec-4 org.springframework.beans.factory.confi";
		String date = rawMsg.substring(0, 10);
		System.out.println("date:"+date);
		System.out.println(System.getProperty("os.name"));
		
         Pattern pattern = Pattern.compile("^(\\d.*?)\\s");
         Matcher matcher = pattern.matcher(rawMsg);
         if(matcher.find()){
        	 String timeStr = matcher.group(1);
        	 System.out.println(timeStr);
        	 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        	// SimpleDateFormat sdf=new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);
        	 SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd",Locale.CHINESE);
        	 try {
				Date dd = sdf.parse(timeStr);
				
				System.out.println(sdf2.format(dd));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
	}
	
	public static void a (){
		String rawMsg="[9/Jan/2017:10:31:26 +0800] - 127.0.0.1 - - GET 500 4665 665 \"http-apr-8080-exec-4\" \"-\" \"-\" \"/SL_POS/\" \"\" \"-\" \"curl/7.29.0\"";
        Pattern pattern = Pattern.compile("^\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(rawMsg);
        if(matcher.find()){
       	 String timeStr = matcher.group(1);
       	 System.out.println(timeStr);
       	 SimpleDateFormat sdf=new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH);
       	// SimpleDateFormat sdf=new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);
       	 SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd",Locale.CHINESE);
       	 try {
				Date dd = sdf.parse(timeStr);
				
				System.out.println(sdf2.format(dd));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

}
