package com.yucl.log.handle.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {		
		ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10000));
			pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					if (!executor.isShutdown()) {
						logger.warn("executes task r in the caller's thread " + r.toString());
						r.run();
					} else {
						logger.warn("the executor has been shut down, the task is discarded " + r.toString());
					}

				}

			});
			
		LogConsumer acclogConsumer = new AccLogConsumer("acclog",pool);
		acclogConsumer.start();
		LogConsumer applogConsumer = new AppLogConsumer("applog",pool);
		applogConsumer.start();
		LogConsumer containerlogConsumer = new ContainerLogConsumer("container",pool);
		containerlogConsumer.start();
		LogConsumer syslogConsumer = new SysLogConsumer("hostsyslog",pool);
		syslogConsumer.start();

	}

}
