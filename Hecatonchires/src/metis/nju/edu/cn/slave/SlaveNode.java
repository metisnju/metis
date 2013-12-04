package metis.nju.edu.cn.slave;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.master.MasterNode;
import org.apache.log4j.Logger;

public class SlaveNode implements Runnable {
	static Logger logger = Logger.getLogger(SlaveNode.class);
	HeartBeatSender heartBeatSender;
	TaskListener taskListener;
	boolean stopFlag;

	public SlaveNode() {
		stopFlag = false;
		heartBeatSender = null;
		taskListener = null;
	}

	public void SetStopFlag() {
		if (heartBeatSender != null)
			heartBeatSender.SetStopFlag();
		if (taskListener != null)
			taskListener.SetStopFlag();
		stopFlag = true;
	}

	boolean Register() {
		Socket socket;
		try {
			logger.debug("slave请求socket");
			socket = new Socket("localhost", Param.MASTERPORT);
			logger.debug("slave请求socket成功");
		} catch (Exception e) {
			logger.error("slave请求socket失败");
			return false;
		}
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(Param.TYPE_REGISTER);
			out.println(Param.CLINENT_ID);
			out.println(Param.CLINENT_PASSWORD);
			out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String data = in.readLine();
			System.out.println(data);
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("slave读取数据失败");
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		logger.debug("slave请求socket");
		// TODO Auto-generated method stub
		if (!Register())
			SetStopFlag();
		if (stopFlag)
			return;
		Thread t1 = new Thread(heartBeatSender = new HeartBeatSender(this),
				"HeartBeatSender");

		Thread t2 = new Thread(taskListener = new TaskListener(this),
				"TaskListener");
		t1.start();
		t2.start();
	}
}
