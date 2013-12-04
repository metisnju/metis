package metis.nju.edu.cn.slave;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import metis.nju.edu.cn.info.Param;

public class HeartBeatSender implements Runnable {
	SlaveNode slaveNode;
	boolean stopFlag;

	static Logger logger = Logger.getLogger(HeartBeatSender.class);

	public boolean HeartBeat() {
		Socket socket;
		try {
			logger.debug("slave请求socket");
			socket = new Socket(Param.SERVER_IP, Param.MASTERPORT);
			logger.debug("slave请求socket成功");
		} catch (Exception e) {
			logger.error("slave请求socket失败");
			return false;
		}
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(Param.TYPE_HEARTBEAT);
			out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String data = in.readLine();
			socket.close();
			if (data.compareTo("OK") == 0) {
				logger.debug("heartBeat成功");
				return true;
			} else {
				logger.debug("heartBeat失败" + data);
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("slave读取数据失败");
			return false;
		}

	}

	public HeartBeatSender(SlaveNode s) {
		stopFlag = false;
		slaveNode = s;
	}

	public void SetStopFlag() {
		logger.debug("setStopFlag");
		stopFlag = true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while ((!stopFlag)) {
			if (!HeartBeat())
				slaveNode.SetStopFlag();
			try {
				Thread.sleep(1000 * Param.TIMEOUT_SLAVE_HEART);
			} catch (InterruptedException e) {
				break;
			}
		}

	}

}
