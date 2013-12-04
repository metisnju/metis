package metis.nju.edu.cn.master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.info.SlaveFactory;

import org.apache.log4j.Logger;

public class MasterListener implements Runnable {

	ServerSocket serverSocket;
	SlaveFactory slaveFactory;
	MasterNode masterNode;
	boolean StopFlag;
	static Logger logger = Logger.getLogger(MasterListener.class);

	public void SetStopFlag() {
		StopFlag = true;
	}

	public MasterListener(SlaveFactory s, MasterNode m) {
		masterNode = m;
		slaveFactory = s;
		StopFlag = false;
		try {
			serverSocket = new ServerSocket(Param.MASTERPORT);
			logger.debug("服务器端口绑定成功");
		} catch (Exception e) {
			serverSocket = null;
			logger.error("服务器端口被占用，绑定失败");
			StopFlag = true;
			masterNode.SetStopFlag();
			return;
		}
	}

	public void run() {
		while (!StopFlag) {
			try {
				logger.debug("服务器等待socket");
				Thread t = new Thread(new SocketFilter(serverSocket.accept(),
						slaveFactory, masterNode), "socketFilter");
				t.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("服务器获取socket失败");
				serverSocket = null;
				return;
			}
		}
	}

}
