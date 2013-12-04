package metis.nju.edu.cn.info;

public class Param {
	public static final int STATE_WAITING = 0;// 标示slave状态为等待
	public static final int STATE_WORKING = 1;// 标示slave状态为工作
	public static final int STATE_DISPATCHING = 2;// 标示slave的状态为调度中
	public static final int TYPE_REGISTER = 0;// 标示数据报类型为注册
	public static final int TYPE_HEARTBEAT = 1;// 标示数据报类型为心跳
	public static final int TYPE_SUBMIT = 2;// 标示数据报类型为提交数据
	public static final int TYPE_GETFILE = 3;// 标示数据报类型为请求文件
	public static final int TYPE_CLOSE = 4;//提交结果
	public static final int BUFFSIZE = 8192;// 缓冲大小
	public static int TIMEOUT_MUSTER = 10;// 标示slave在server端被丢弃的时间 单位为秒
	public static int TIMEOUT_SLAVE_HEART = 5;// 标示salve进行一次心跳的时间 单位为秒
	public static String CLINENT_ID = "gongbao";// 标示client的账户
	public static String CLINENT_PASSWORD = "aaaaaa";// 标示client的密码
	public static String SERVER_IP = "localhost";// server的IP地址
	public static int MASTERPORT = 8123; // master用的端口
	public static int SLAVEPORT = 8122;// slave用的端口
	public static int TASK_LIMIT = 5;// 标示task的数量限制
	public static int SLEEP_MASTER_LISTENER = 50;// MasterListener发出socket后等待对方读取数据的等待时间，
													// 单位为毫秒
	public static int SLEEP_Task_PRODUCER = 1000;// Producer插入失败后等待时间 单位为毫秒
	public static int SLEEP_Task_DIVIDER = 1000;// Divider获取失败后等待时间 单位为毫秒
}
