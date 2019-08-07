package com.tarena.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * 该类是一个工具类，负责读写数据
 * 把读写逻辑单独定义在该类中的目的是
 * 为了重用这些逻辑。
 * @author sige
 *
 */
public class IOUtil {
  /**
   * 从给定的文件中读取第一行字符串，并将其
   * 转换为一个long值返回
   * @param file
   * @return
   */
  public static long readLong(File file){
	  BufferedReader br = null;
	  try {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
	    br = new BufferedReader(isr);
		String line = br.readLine();
		long l = Long.parseLong(line);
		return l;
	} catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}finally {
		try {
			if(br != null){
			    br.close();
			}
		} catch (IOException e) {
		}
	}
  }
  
  /**
   * 从给定的RandomAccessFile的当前位置处连续读取给定字节数，并转换为字符串。
   * @param raf
   * @param len
   * @return
   */
  public static String readString(RandomAccessFile raf,int len)throws IOException{
	  byte[] buf = new byte[len];
	  raf.read(buf);
	  String str = new String(buf,"ISO8859-1");
	  return str.trim();
  }
  
  /**
   * 从给定的RandomAccessFile当前位置处读取一个int值并返回
   * @param raf
   * @return
   */
  public static int readInt(RandomAccessFile raf)throws IOException{
	  return raf.readInt();
  }
  
  /**
   * 从给定的RandomAccessFile当前位置处读取一个short值并返回
   * @param raf
   * @return
   * @throws IOException
   */
  public static short readShort(RandomAccessFile raf)throws IOException{
	  return raf.readShort();
  }
  
}
