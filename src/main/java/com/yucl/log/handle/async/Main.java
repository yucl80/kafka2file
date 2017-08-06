package com.yucl.log.handle.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {		
		ThreadPoolExecutor pool = new ThreadPoolExecutor(40, 80, 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000));
			pool.setRejectedExecutionHandler((r, executor) -> {
                if (!executor.isShutdown()) {
                    logger.warn("executes task r in the caller's thread " + r.toString());
                    r.run();
                } else {
                    logger.warn("the executor has been shut down, the task is discarded " + r.toString());
                }

            });
			
		LogConsumer acclogConsumer = new AccLogConsumer("acclog",pool);
		acclogConsumer.start();
		LogConsumer applogConsumer = new AppLogConsumer("applog",pool);
		applogConsumer.start();
		LogConsumer containerlogConsumer = new ContainerLogConsumer("containerlog",pool);
		containerlogConsumer.start();
		LogConsumer syslogConsumer = new SysLogConsumer("hostsyslog",pool);
		syslogConsumer.start();

	}

}
