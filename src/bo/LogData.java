package bo;
/**
 * LogData的每一个实例用于表示wtmpx文件中的一条日志信息
 * @author sige
 *
 */
public class LogData {
   /**
    * 日志在wtmpx文件中的长度
    * 每一条日志的长度都是372字节	
    */
   public static final int LOG_LENGTH=372;
   /**
    * user在单条日志的起始字节	
    */
   public static final int USER_OFFSET=0;
   /**
    * user在日志中占用的字节量
    */
   public static final int USER_LENGTH=32;
   /**
    * PID在日志中的起始位置
    */
   public static final int PID_OFFSET=68;
   /**
    * TYPE在日志中的起始位置
    */
   public static final int TYPE_OFFSET=72;
   /**
    * TIME在日志中的起始位置
    */
   public static final int TIME_OFFSET=80;
   /**
    * HOST在日志中的起始位置
    */
   public static final int HOST_OFFSET=114;
   /**
    * HOST在日志文件中的长度
    */
   public static final int HOST_LENGTH=258;
   /**
    * 日志类型：登入操作
    */
   public static final short TYPE_LOGIN=7;
   /**
    * 日志类型：登出操作
    */
   public static final short TYPE_LOGOUT=8;
	
   //登录用户的用户名
   private String user;
   //进程ID
   private int pid;
   //日志类型(登入或登出)
   private short type;
   //日志生成的时间(登入和登出的时间)
   private int time;
   //登录用户的IP地址
   private String host;
   public LogData(String user, int pid, short type, int time, String host) {
		super();
		this.user = user;
		this.pid = pid;
		this.type = type;
		this.time = time;
		this.host = host;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * 
	 */
	public String toString() {
		return user+","+pid+","+type+","+time+","+host;
	} 
} 
