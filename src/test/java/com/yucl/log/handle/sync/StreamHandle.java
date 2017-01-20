package com.yucl.log.handle.sync;

import java.io.OutputStream;

public class StreamHandle {
	private long lastWriteTime;
	private OutputStream outputStream;

	public StreamHandle(OutputStream outputStream) {
		this.setOutputStream(outputStream);
	}

	public StreamHandle(OutputStream outputStream, long lastWriteTime) {
		this.setOutputStream(outputStream);
		this.setLastWriteTime(lastWriteTime);
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

	public void setLastWriteTime(long lastWriteTime) {
		this.lastWriteTime = lastWriteTime;
	}

}
