package com.yucl.log.handle.async;

import java.io.IOException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousFileChannel;

public class ChannelWrapper implements AsynchronousChannel {
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


	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}


	

}
