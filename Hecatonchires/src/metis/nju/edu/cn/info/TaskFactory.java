package metis.nju.edu.cn.info;

import java.util.TreeSet;

import org.apache.log4j.Logger;

public class TaskFactory {
	TreeSet<TaskInfo> taskList;
	static Logger logger = Logger.getLogger(TaskFactory.class);
	int index;
	int size;

	public TaskFactory() {
		index = 0;
		size = 0;
		taskList = new TreeSet<TaskInfo>();
	}

	public synchronized int getSize() {
		return size;
	}

	public synchronized int getNeedSize() {
		return Param.TASK_LIMIT - size;
	}

	public synchronized boolean insert(TaskInfo taskInfo) {
		if (size < Param.TASK_LIMIT) {
			taskInfo.index = index++;
			if (taskList.add(taskInfo.clone())) {// must use clone function
				logger.debug("task插入成功");
				++size;
				return true;
			} else {
				logger.error("task插入发生异常");
				return false;
			}

		} else {
			logger.warn("taskFactory 已填满，插入拒绝");
			return false;
		}
	}

	/*
	 * public synchronized boolean insert(int level, int id, String data){ if
	 * (size < Param.TASK_LIMIT){ if (taskList.add(new TaskInfo(level, id, data,
	 * index++))){ logger.debug("task插入成功"); ++size; return true; }else{
	 * logger.error("task插入发生异常"); return false; }
	 * 
	 * }else{ logger.warn("taskFactory 已填满，插入拒绝"); return false; } }
	 */
	public synchronized TaskInfo getTask() {
		if (size > 0) {
			--size;
			TaskInfo taskInfo = taskList.first();
			taskList.remove(taskInfo);
			return taskInfo;
		}
		logger.debug("taskFactory 为空，请求拒绝");
		return null;
	}
}
