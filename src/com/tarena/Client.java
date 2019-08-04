package com.tarena;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import com.tarena.util.IOUtil;

/**
 * 客户端应用程序
 * 运行在UNIX系统上
 * 作用是定期读取系统日志文件WTMPX文件
 * 收集每个用户的登入登出日志，将匹配成对的日志信息发送至服务器端。
 * @author sige
 *
 */
public class Client {
   //unix系统日志文件  wtmpx文件	
   private File logFile;
   
   //保存每次解析后的日志文件
   private File textLogFile;
   
   //保存每次解析日志文件后的位置(书签)的文件
   private File lastPositionFile;
   
   //每次从wtmpx文件中解析日志的条数
   private int batch;
   
   /**
    * 构造方法中初始化
    */
   public Client(){
	   try {
		 this.batch = 10;
		 logFile = new File("wtmpx");
		 lastPositionFile = new File("last-position.txt");
		 textLogFile = new File("log.txt");
		
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
   }
   /**
    * 该方法为第一大步的第二小步的逻辑
    * 用于检查wtmpx文件是否还有数据可以读取
    * 
    * @return -1:没有数据可读了
    *         其他数字：继续读取 的位置
    */
   public long hasLogs(){
	   try {
		   //默认从文件开始读取
		   long lastposition = 0;
		   /*
		    * 这里有两种情况
		    * 1:没有找到last-position.txt
		    *   文件，这说明从来没有读取过wtmpx
		    * 2:有last-position.txt文件
		    *   那么，就从该文件记录的位置开始读取
		    */
		   if(lastPositionFile.exists()){
			   lastposition = IOUtil.readLong(lastPositionFile);
		   }
		   /*
		    * 必要判断,wtmpx文件的总大小
		    * 减去这次准备开始读取的位置，应当
		    * 大于一条日志所占用的字节量(372)
		    */
		   if(logFile.length()-lastposition<372){
			   lastposition = -1;
		   }
		   
		   return lastposition;
	} catch (Exception e) {
		e.printStackTrace();
		return -1;
	}
   }
   /**
    * 判断当前RandomAccessFile读取的位置
    * 是否在wtmpx文件中还有内容可读
    * @param raf
    * @return
 * @throws IOException 
    */
   public boolean hasLogsByStep(RandomAccessFile raf) throws IOException{
	   if(logFile.length()-raf.getFilePointer()>=372){
		   return true;
	   }else{
		   return false;
	   }
   }
   /**
    * 第一大步：
    * 从wtmpx文件中读取batch条日志
    * 并解析为batch行字符串，每行字符串表示
    * 一条日志，然后写入log.txt文件中。
    * @return true: 解析成功
    *         false:解析失败
    */
   public boolean readNextLogs(){
	   /*
	    * 解析步骤：
	    * 1:首先先判断wtmpx文件是否存在
	    * 2:判断是否还有新数据可读
	    * 3:从上一次读取的位置开始继续读取
	    * 4:循环batch次，读取batch个372字节
	    *   并转换为batch个日志
	    * 5:将解析后的batch个日志写入log.txt文件中。
	    */
	   //1
	   if(!logFile.exists()){
		   return false;
	   }
	   //2
	   long lastposition = hasLogs();
	   if(lastposition<0){
		   return false;
	   }
	   //预留一个判断
	   /*
	    * 
	    */
	   try {
		 //创建RandomAccessFile来读取日志文件
		 RandomAccessFile  raf = new RandomAccessFile(logFile, "r");
		 //移动游标到指定位置，开始继续读取
		 raf.seek(lastposition);
		 //循环batch次，解析batch条日志
		 for(int i=0;i<batch;i++){
			 /*
			  * 首先判断是否还有日志可以读
			  */
			 if(!hasLogsByStep(raf)){
				 break;
			 }
		 }
	} catch (Exception e) {
		e.printStackTrace();
	}
	   
	   return false;
   }
   
   /**
    * 客户端开始工作的方法
    */
   public void start(){
	   /*
	    * 开始方法中，我们要循环以下三个步骤
	    * 1:从wtmpx文件中一次解析batch条日志
	    * 2:将解析后的日志和上次没有匹配的日志一起匹配成对
	    * 3:将匹配成对的日志发送至服务端
	    */
	   //1
	   readNextLogs();
	   
   }
   
   public static void main(String[] args) {
	  Client client = new Client();
	  client.start();
   }
   
}
