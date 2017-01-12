package com.yucl.log.handle.async;

import java.nio.channels.AsynchronousFileChannel;

public class ChannelWrapper {
	private long lastWriteTime;
	private long pos;
	private AsynchronousFileChannel fileChannel;

	public ChannelWrapper(AsynchronousFileChannel fileChannel,long pos) {
		this.setFileChannel(fileChannel);
		this.setPos(pos);
	}

	
	public long getLastWriteTime() {
		return lastWriteTime;
	}

	public void setLastWriteTime(long lastWriteTime) {
		this.lastWriteTime = lastWriteTime;
	}


	public AsynchronousFileChannel getFileChannel() {
		return fileChannel;
	}



	public void setFileChannel(AsynchronousFileChannel fileChannel) {
		this.fileChannel = fileChannel;
	}


	public long getPos() {
		return pos;
	}


	public void setPos(long pos) {
		this.pos = pos;
	}

}
