package com.learn.kafka;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yucl.log.handle.async.AppLogConsumer;

public class C {
	private static final Logger logger = LoggerFactory.getLogger(AppLogConsumer.class);

	public static void main(String[] args) {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(2500));
		// pool.setRejectedExecutionHandler(new
		// ThreadPoolExecutor.CallerRunsPolicy());
		pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {				
				if (!executor.isShutdown()) {
					logger.warn("executes task r in the caller's thread " + r.toString());
					r.run();
				}else{
					logger.warn("the executor has been shut down, the task is discarded " + r.toString());
				}

			}

		});

		Path path = FileSystems.getDefault().getPath("", "a.txt");
		long len = 0;
		try {
			AsynchronousFileChannel asyncFile = AsynchronousFileChannel.open(path,
					new HashSet<StandardOpenOption>(Arrays.asList(StandardOpenOption.WRITE, StandardOpenOption.CREATE)),
					pool);
			int bs = 1024 * 64;
			byte[] bytes = new byte[bs];
			for (int i = 0; i < bs; i++) {
				bytes[i] = 'a';
			}
			long begin = System.currentTimeMillis();
			for (int i = 0; i < 100000; i++) {
				asyncFile.write(ByteBuffer.wrap(bytes), len);
				len += bytes.length;
			}
			long end = System.currentTimeMillis();
			System.out.println(len / 1024 / 1024 + "M : " + len / 1024 / 1024 * 1000 / (end - begin) + "M/s");
			begin = System.currentTimeMillis();
			asyncFile.force(false);
			asyncFile.close();
			end = System.currentTimeMillis();
			System.out.println("close time:" + (end - begin));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
