package com.yucl.log.handle.sync;

public class Main {

	public static void main(String[] args) {
		AccLogConsumer acclogConsumer = new AccLogConsumer("acclog");
		acclogConsumer.start();
	    AppLogConsumer applogConsumer = new AppLogConsumer("applog");
	    applogConsumer.start();


	}

}
