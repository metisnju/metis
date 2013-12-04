package metis.nju.edu.cn.master;

import java.io.IOException;
import java.net.ServerSocket;

import metis.nju.edu.cn.info.SlaveFactory;
import metis.nju.edu.cn.info.TaskFactory;
import metis.nju.edu.cn.test.logTest;

import org.apache.log4j.Logger;

public class MasterNode implements Runnable {

	/**
	 * @param args
	 */
	static Logger logger = Logger.getLogger(MasterNode.class);
	SlaveFactory slaveFactory;
	TaskFactory taskFactory;
	MasterListener masterListener;
	TaskDivider taskDivider;
	TaskProducer taskProducer;
	boolean StopFlag;

	public MasterNode() {
		StopFlag = false;
		slaveFactory = new SlaveFactory();
		taskFactory = new TaskFactory();
		masterListener = new MasterListener(slaveFactory, this);
		taskProducer = new TaskProducer(this, slaveFactory, taskFactory);
		taskDivider = new TaskDivider(this, slaveFactory, taskFactory);
	}

	public void SetStopFlag() {
		StopFlag = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (StopFlag)
			return;
		Thread t1 = new Thread(masterListener, "MasterListener");
		t1.start();
		Thread t2 = new Thread(taskProducer, "TaskProducer");
		t2.start();
		Thread t3 = new Thread(taskDivider, "taskDivider");
		t3.start();
	}

}
