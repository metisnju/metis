package metis.nju.edu.cn.master;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.info.SlaveFactory;
import metis.nju.edu.cn.info.TaskFactory;
import metis.nju.edu.cn.info.TaskInfo;

import org.apache.log4j.Logger;

public class TaskDivider implements Runnable {

	MasterNode masterNode;
	boolean StopFlag;
	SlaveFactory slaveFactory;
	TaskFactory taskFactory;
	static Logger logger = Logger.getLogger(TaskDivider.class);

	public void setStopFlag() {
		StopFlag = true;
	}

	public TaskDivider(MasterNode m, SlaveFactory s, TaskFactory t) {
		StopFlag = false;
		slaveFactory = s;
		taskFactory = t;
		masterNode = m;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String ip = null;
		while (!StopFlag) {// 如果未停止
			if (ip == null)
				ip = slaveFactory.getSlave();
			if (ip != null) {
				TaskInfo taskInfo = taskFactory.getTask();
				if (taskInfo != null) {
					if (slaveFactory.setWork(ip, taskInfo)) {
						logger.debug("分配task给：" + ip);
						Thread t = new Thread(new TaskSender(ip, taskInfo),
								"taskSender");
						t.start();
						logger.debug("分配task完成");
					}
					ip = null;
					continue;
				} else {
					logger.debug("获取task失败");
				}
			} else {
				logger.debug("获取slave失败");
			}
			try {
				Thread.sleep(Param.SLEEP_Task_DIVIDER);
			} catch (Exception e) {
				logger.warn("sleep 失败 ");
			}
		}
	}

	private void SendTask(String ip, TaskInfo taskInfo) {
		// TODO Auto-generated method stub

	}

}