package metis.nju.edu.cn.info;

import java.util.HashMap;
import java.util.Stack;
import java.util.TreeMap;

import metis.nju.edu.cn.test.logTest;

import org.apache.log4j.Logger;

public class SlaveFactory extends Object {
	HashMap<String, SlaveInfo> slaveList;// 储存slave的信息
	TreeMap<Order, String> orderList;// 等待任务的ip序列
	Stack<TaskInfo> taskList;
	static Logger logger = Logger.getLogger(SlaveFactory.class);
	int index;

	class Order implements Comparable {
		/*
		 * 用来排序的类，以实现给order最低的slave优先分配工作
		 */
		int level;
		int index;

		public Order(int l, int i) {
			level = l;
			index = i;
		}

		public boolean equals(Object obj) {
			Order b = (Order) obj;
			return (level == b.level) && (index == b.index);
		}

		@Override
		public int compareTo(Object arg0) {
			Order b = (Order) arg0;
			if (level < b.level)
				return level - b.level;
			return index - b.index;
		}
	}

	public SlaveFactory() {
		slaveList = new HashMap<String, SlaveInfo>();
		orderList = new TreeMap<Order, String>();
		taskList = new Stack<TaskInfo>();
		index = 0;
	}

	/*
	 * 进行一次心跳
	 */
	public synchronized boolean heartBeat(String ip) {
		SlaveInfo s;
		if ((s = slaveList.get(ip)) != null) {
			logger.debug("有效 ip进行heartBeat:" + ip);
			s.lastHeartBeat = SlaveInfo.getTime();
			slaveList.put(ip, s);
			return true;
		} else {
			logger.warn("无效ip进行heartBeat:" + ip);
			return false;
		}
	}
	public synchronized SlaveInfo getSlaveByIp(String ip) {// 把ip对应的slave标示为工作中
		SlaveInfo slaveInfo = slaveList.get(ip);
		if (slaveInfo == null) {
			logger.warn("为" + ip + "获取正在处理的工作，但是ip没有在Factory中");
			return null;
		}
		if (slaveInfo.state != Param.STATE_WORKING) {
			logger.warn("为" + ip + "获取正在处理的工作，但是ip并不是工作中");
			remove(ip);
			return null;
		}
		return slaveInfo;
	}
	//获取ip正在处理的task
	public synchronized TaskInfo getTask(String ip) {// 把ip对应的slave标示为工作中
		SlaveInfo slaveInfo = slaveList.get(ip);
		if (slaveInfo == null) {
			logger.warn("为" + ip + "获取正在处理的工作，但是ip没有在Factory中");
			return null;
		}
		if (slaveInfo.state != Param.STATE_WORKING) {
			logger.warn("为" + ip + "获取正在处理的工作，但是ip并不是工作中");
			remove(ip);
			return null;
		}
		return slaveInfo.taskInfo;
	}
	public synchronized boolean setUnwork(String ip) {// 把ip对应的slave标示为工作中
		SlaveInfo slaveInfo = slaveList.get(ip);
		if (slaveInfo == null) {
			logger.warn("为" + ip + "设置非工作，但是ip没有在Factory中");
			return false;
		}
		if (slaveInfo.state != Param.STATE_WORKING) {
			logger.warn("为" + ip + "设置非工作，但是ip并不是工作中");
			remove(ip);
			return false;
		}
		slaveInfo.state = Param.STATE_WAITING;
		slaveInfo.taskInfo = null;
		orderList.put(new Order(slaveInfo.level, slaveInfo.index), slaveInfo.ip);
		return true;
	}
	public synchronized boolean setWork(String ip, TaskInfo taskInfo) {// 把ip对应的slave标示为工作中
		SlaveInfo slaveInfo = slaveList.get(ip);
		if (slaveInfo == null) {
			logger.warn("为" + ip + "设置工作，但是ip没有在Factory中");
			return false;
		}
		if (slaveInfo.state != Param.STATE_DISPATCHING) {
			logger.warn("为" + ip + "设置工作，但是ip并不是分配中");
			remove(ip);
			return false;
		}
		slaveInfo.state = Param.STATE_WORKING;
		slaveInfo.taskInfo = taskInfo;
		orderList.remove(new Order(slaveInfo.level, slaveInfo.index));
		return true;
	}

	public synchronized String getSlave() {// 返回一个有效的slave的ip如果没有，返回null
		while (!orderList.isEmpty()) {
			Order order = orderList.firstKey();
			String ip = orderList.firstEntry().getValue();// 获取ip
			logger.debug("getSlave:" + ip);
			if (!slaveList.get(ip).isTimeOut()) {// 检查这个ip是否超时
				logger.debug("Slave未超时:" + ip);
				orderList.remove(order);
				slaveList.get(ip).state = Param.STATE_DISPATCHING;
				return ip;
			} else {
				logger.warn("Slave超时:" + ip);
				remove(ip);
			}

		}
		return null;
	}

	public synchronized boolean register(String ip, String id, String password) {
		logger.debug("新用户登录,ip:" + ip + ";id:" + id + ";password:" + password);
		int level = getLevel(id, password);
		if (level < 0) {
			logger.warn("用户登录失败,账号密码不匹配,ip:" + ip + ";id:" + id + ";password:"
					+ password);
			logger.debug("新用户登录,ip:" + ip + ";id:" + id + ";password:"
					+ password);
			return false;
		}
		if (slaveList.containsKey(ip)) {
			logger.warn("ip重复登录,删除之前注册信息,ip:" + ip + ";id:" + id + ";password:"
					+ password);
			remove(ip);
		}
		logger.debug("用户登录成功,ip:" + ip + ";id:" + id + ";password:" + password);
		insert(ip, new SlaveInfo(ip, id, password, level, index++));
		return true;
	}

	/*
	 * 把ip标示的slave删除，注意要把这个slave的工作重新加入工作队列中
	 */
	void insert(String ip, SlaveInfo slaveInfo) {
		logger.debug("开始插入ip:" + ip);
		if (slaveList.get(ip) == null) {
			slaveList.put(ip, slaveInfo);
			Order order = new Order(slaveInfo.level, slaveInfo.index);
			orderList.put(order, ip);
		} else {
			logger.error("重复插入ip:" + ip);
		}
		logger.debug("结束插入ip:" + ip);
	}

	/*
	 * 把ip标示的slave删除，注意要把这个slave的工作重新加入工作队列中
	 */
	void remove(String ip) {
		logger.debug("开始删除ip:" + ip);
		SlaveInfo slaveInfo = slaveList.get(ip);
		if (slaveInfo != null) {
			if (slaveInfo.state == Param.STATE_WORKING) {
				slaveList.remove(slaveInfo);
				/*
				 * 任务放入未完成序列任务
				 */
				taskList.push(slaveInfo.taskInfo);
			} else if (slaveInfo.state == Param.STATE_WAITING) {
				// 处于等待状态，从等待序列中移除
				orderList.remove(new Order(slaveInfo.level, slaveInfo.index));
			}
		} else {
			logger.warn("未注册ip:" + ip);
		}
		logger.debug("结束删除ip:" + ip);
	}

	/*
	 * 获取id标示的用户的用户等级，如果密码账号匹配失败，则返回-1
	 */
	int getLevel(String id, String password) {
		/*
		 * 检查数据库，返回用户等级
		 */
		return 0;
	}

	public synchronized void recycle(Stack<TaskInfo> tasks) {
		while (!taskList.empty()) {
			tasks.push(taskList.pop());
		}
	}
}
