package metis.nju.edu.cn.info;

public class SlaveInfo {

	/**
	 * @param args
	 *            用于在master端保存每个slave的信息，包括ip，id, lastHeartBeat等
	 *            rank表示是这个评测机的优先级 index表示这个评测机加入评测机列表的顺序
	 *            排序的时候，首先从用户优先级低的评测机中选择（越可信的id有越低的优先级） 如果优先级相同，则按加入评测机的顺序来排序
	 */

	public String ip;// slave的ip地址
	public String id;// slave的账号
	public String password;// slave的password
	public int level;// slave的等级
	public int state;// slave 的状态
	public long lastHeartBeat;// 心跳时间
	public int index;
	public TaskInfo taskInfo;

	public SlaveInfo(String Ip, String Id, String Password, int Level, int Index) {
		ip = Ip;
		id = Id;
		level = Level;
		index = Index;
		password = Password;
		state = Param.STATE_WAITING;
		taskInfo = null;
		lastHeartBeat = getTime();
	}

	public static long getTime() {// 获取当前时间
		return System.currentTimeMillis();
	}

	public boolean isTimeOut() {
		return getTime() - lastHeartBeat > Param.TIMEOUT_MUSTER * 1000;
	}
}
