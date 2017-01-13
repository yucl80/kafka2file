package com.yucl.log.handle.async;

public class Main {

	public static void main(String[] args) {
	/*	AccLogConsumer acclogConsumer = new AccLogConsumer("acclog");
		acclogConsumer.start();
		AppLogConsumer applogConsumer = new AppLogConsumer("applog");
		applogConsumer.start();
		ContainerLogConsumer containerlogConsumer = new ContainerLogConsumer("container");
		containerlogConsumer.start();*/
		
		 SysLogConsumer syslogConsumer = new SysLogConsumer("hostsyslog");
		 syslogConsumer.start();
		 

	}

}
