package metis.nju.edu.cn.master;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.info.TaskInfo;

public class TaskSender implements Runnable {
	String ip;
	static Logger logger = Logger.getLogger(TaskSender.class);
	TaskInfo taskInfo;

	TaskSender(String Ip, TaskInfo t) {
		ip = Ip;
		taskInfo = t;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// System.out.println(taskInfo.data);
		try {
			Socket socket = new Socket(ip.substring(1), Param.SLAVEPORT);
			logger.debug("任务分配成功ip:" + ip);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			System.out.println(taskInfo.data);
			out.println(taskInfo.data);
			out.flush();
			Thread.sleep(Param.SLEEP_MASTER_LISTENER);
			socket.close();
		} catch (Exception e) {
			logger.debug("Socket建立失败");
		}
	}

}
