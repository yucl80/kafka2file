package com.learn.kafka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class AsynchronousFileChannelTest {

	public static void main(String[] args) {	
		AsynchronousFileChannelTest aft = new AsynchronousFileChannelTest();
		try {
			aft.writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void writeFile() throws IOException {
		
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<1000;i++){
			sb.append("aaaaaaaaa");
		}
		String input = sb.toString();
		System.out.println("thread id: "+Thread.currentThread().getId()+" ,  + Input string: " + input);
		byte[] byteArray = input.getBytes();
        System.out.println("total:"+byteArray.length+" thread id: "+Thread.currentThread().getId());
		ByteBuffer buffer = ByteBuffer.wrap(byteArray);

		Path path = Paths.get("writefile.txt");
		Set<OpenOption> optionsSet = new HashSet<OpenOption>();
		optionsSet.add(StandardOpenOption.CREATE);
		optionsSet.add(StandardOpenOption.WRITE);
		AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, optionsSet,null);
         
		CompletionHandler handler = new CompletionHandler() {

			@Override
			public void completed(Object result, Object attachment) {

				System.out.println(+System.currentTimeMillis()+"thread id: "+Thread.currentThread().getId()+" attachment:"+ attachment+" completed and " + result + " bytes are written.");
			}

			@Override
			public void failed(Throwable e, Object attachment) {

				System.out.println("thread id: "+Thread.currentThread().getId()+attachment + " failed with exception:");
				e.printStackTrace();
			}

		};
		System.out.println("before:"+channel.size());
		 channel.write(buffer, channel.size(),"hello",handler);
		System.out.println("after:"+channel.size()+" :"+System.currentTimeMillis());
		//channel.write(buffer, channel.size(), "Write operation ALFA", handler);
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		channel.close();

		
		
		
		//printFileContents(path.toString());
	}

	private void printFileContents(String path) throws IOException {

		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);

		String textRead = br.readLine();
		System.out.println("File contents: ");

		while (textRead != null) {

			System.out.println("     " + textRead);
			textRead = br.readLine();
		}

		fr.close();
		br.close();
	}
}
