package com.tarena;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 服务端应用程序
 * @author sige
 *
 */
public class Server {
   //运行在服务端的Socket
	private ServerSocket server;
	//线程池，用于管理客户端连接的交互线程
	private ExecutorService threadPool;
	//保存所有客户端发送过来的配对日志的文件
	private File serverLogFile;
	//创建一个双缓冲队列，用于存储配对日志。
	private BlockingQueue<String> messageQueue;
	/**
	 * 构造方法，用于初始化服务端
	 * @throws IOException 
	 */
	public Server() throws Exception{
	   try {
		/*
		 * 创建ServerSocket时需要指定服务端口 
		 */
		System.out.println("初始化服务端");
		server = new ServerSocket(8088);
		//初始化线程池
		threadPool = Executors.newFixedThreadPool(50);
		//初始化保存日志的文件
		serverLogFile = new File("server-log.txt");
		//初始化缓冲队列
		messageQueue = new LinkedBlockingQueue<String>();
		
		System.out.println("服务端初始化完毕");
	   } catch (Exception e) {
		    e.printStackTrace();
			throw e;
	   }
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void  start(){
		try {
			/*
			 * 将写日志文件的线程启动起来
			 */
			WriteLogThread  thread = new WriteLogThread();
			thread.start();
			/*
			 * ServerSocket的accept方法
			 * 用于监听8088端口，等待客户端的连接
			 * 该方法是一个阻塞方法，直到一个客户端
			 * 连接，否则该方法一直阻塞。若一个客户端
			 * 连接了，会返回该客户端的Socket
			 */
			while(true){
					System.out.println("等待客户端连接...");
					Socket socket = server.accept();
					/*
					 * 当一个客户端连接后，启动一个线程
					 * ClientHandler,将该客户端的
					 * Socket传入，使得该线程处理与该
					 * 客户端的交互。
					 * 这样，我们能再次进入循环，接收
					 * 下一个客户端的连接了。
					 */
				    Runnable  handler = new ClientHandler(socket);
//				    Thread t = new Thread(handler);
//				    t.start();	
				    /*
				     * 使用线程池分配空闲线程来处理
				     * 当前连接的客户端
				     */
				    threadPool.execute(handler);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server;
		try {
			server = new Server();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务端初始化失败");
		}
		
	}
	
	/**
	 * 服务端中的一个线程，用于与某个客户端交互。
	 * 使用线程的目的是使得服务端可以处理多客户端了。
	 * @author sige
	 *
	 */
	class ClientHandler implements  Runnable{
		//当前线程处理的客户端的Socket
		private Socket socket;

		/**
		 * 根据给定的客户端的Socket,创建线程体
		 * @param socket
		 */
		public ClientHandler(Socket socket){
			this.socket = socket;
		}
		/**
		 * 该线程会将当前Socket中的输入流获取
		 * 用来循环读取客户端发送过来的消息。
		 */
		public void run(){
			/*
			 * 定义在try语句外的目的是，为了在finally中也可以引用到
			 */
			PrintWriter pw = null;
			try{
				/*
				 * 为了让服务端与客户端发送信息，
				 * 我们需要通过socket获取输出流.
				 */
				OutputStream out = socket.getOutputStream();
				//转换为字符流，用于指定编码集
				OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");
				//创建缓冲字符输出流
				pw = new PrintWriter(osw,true);
	
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in,"UTF-8");
				BufferedReader br = new BufferedReader(isr);						
				String message = null;
				/*
				 * 循环读取客户端发送过来的每一组配对日志
				 * 读取到一组，就将该日志存入消息队列，等待被写入文件。
				 */
				while((message = br.readLine())!=null){
					/*
					 * 若读取到客户端发送的内容是"over"表示客户端发送
					 * 完毕所有日志了，应当停止再接受客户端发送的内容了
					 */
					if("over".equals(message)){
						break;
					}
                    messageQueue.offer(message);
				}
				/*
				 * 当退出循环，说明所有客户端发送的日志均接受成功，并存入了消息队列中。
				 * 那么我们回复客户端"OK"
				 */
				pw.println("OK");
			}catch (Exception e) {
				//在Windows中的客户端，报错通常是因为客户端断开了连接
				pw.println("ERROR");
			}finally {
				/*
				 * 无论是linux用户还是windows
				 * 用户，当与服务端断开连接后
				 * 我们都应该在服务端与客户端
				 * 断开连接
				 */
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			
			
		}
	}
	/**
	 * 该线程在Server中仅有一个实例
	 * 作用是：
	 *    循环从消息队列中取出一个配对日志，并写入server-log.txt文件中
	 *    当队列中没有日志后，就休眠一段时间等待客户端发送新的日志过来
	 * @author sige
	 *
	 */
	class WriteLogThread extends Thread{
		public void run(){
			try {
				PrintWriter pw = new PrintWriter(serverLogFile);
				while(true){
					if(messageQueue.size()>0){
						String log = messageQueue.poll();
						pw.println(log);
					}else{
						pw.flush();
						Thread.sleep(5000);
					}
					
				}
			} catch (Exception e) {
				 e.printStackTrace();
			}
		}
	}
	
}
