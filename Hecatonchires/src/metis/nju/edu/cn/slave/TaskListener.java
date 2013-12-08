package metis.nju.edu.cn.slave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import metis.nju.edu.cn.info.Param;

import org.apache.log4j.Logger;

public class TaskListener implements Runnable {
	static Logger logger = Logger.getLogger(TaskListener.class);
	SlaveNode slaveNode;
	boolean stopFlag;
	ServerSocket serverSocket;

	public TaskListener(SlaveNode s) {
		stopFlag = false;
		slaveNode = s;
	}

	public void SetStopFlag() {
		stopFlag = true;
	}

	/*
	 * public boolean SubmitAns(String s){ Socket socket; try {
	 * logger.debug("slave请求socket"); socket = new Socket("localhost",
	 * Param.MASTERPORT); logger.debug("slave请求socket成功"); } catch (Exception e)
	 * { logger.error("slave请求socket失败"); return false; } try { PrintWriter out
	 * = new PrintWriter(socket.getOutputStream(), true);
	 * out.println(Param.TYPE_SUBMIT); out.println(s); out.flush();
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(socket.getInputStream())); String data = in.readLine();
	 * System.out.println(data); socket.close(); return data.compareTo("OK") ==
	 * 0; } catch (Exception e) { // TODO Auto-generated catch block
	 * logger.error("slave读取数据失败"); return false; } }
	 */
	
	//上传评测结果
	public boolean submit(String filename, String newname) {
		Socket socket;
		PrintWriter out;
		BufferedReader in;
		
		//简历连接
		try {
			logger.debug("slave请求socket");
			socket = new Socket(Param.SERVER_IP, Param.MASTERPORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out.println(Param.TYPE_SUBMIT);
			out.flush();
			out.println(newname);
			out.flush();
			logger.debug("slave请求socket成功");
		} catch (Exception e) {
			logger.error("slave请求socket失败");
			return false;
		}
		File file = new File("./sandbox/" + filename);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String s = "";
				while ((s = reader.readLine()) != null) {
					out.write(s + "1\n");
					out.flush();
				}
				out.write("0\n");
				out.flush();
				reader.close();
			} catch (Exception e) {
				logger.error("reading file failed " + filename);
			}
		} else {
			out.println("0\n");
			out.flush();
		}
		try {
			in.readLine();
			socket.close();
		} catch (Exception e) {
			logger.error("未知错误");
		}
		return true;

	}
	
	//拷贝本地文件到sandbox中，如果文件缺失，向master请求
	public boolean cp(String orgname, String servername, String newname) {
		logger.debug("cp:" + orgname + "(" + servername + ") to " + newname);
		File file = new File(orgname);
		if (!file.exists()) {// 向master请求文件
			new File(file.getParent()).mkdirs();
			DataInputStream dis = null;
			DataOutputStream dos = null;
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			Socket socket = null;
			try {
				socket = new Socket(Param.SERVER_IP, Param.MASTERPORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println(Param.TYPE_GETFILE);
				out.println(servername);
				out.flush();
				dis = new DataInputStream(new BufferedInputStream(
						socket.getInputStream()));
				dos = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(orgname)));

				int read = 0;
				while ((read = dis.read(buf)) != -1) {
					dos.write(buf, 0, read);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("获取文件失败" + orgname);
			} finally {
				try {
					if (dos != null) {
						dos.close();
					}
					if (dis != null) {
						dis.close();
					}
					if (socket != null) {
						socket.close();
					}
					Process process = Runtime.getRuntime().exec(
							"sudo chmod 777 " + orgname);
					process.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			new File(new File(newname).getParent()).mkdirs();
			Process process = Runtime.getRuntime().exec(
					"cp " + orgname + " " + newname);
			process.waitFor();
		} catch (Exception e) {
			logger.error("拷贝文件失败" + orgname + " " + newname);
			return false;
		}
		return true;
	}
	//根据dir文件中的结果报告结果
	//4 TIE
	//5 player1 win
	//6 player2 win
	//3 judge error
	public boolean report(String dir) {

		int state = 3;
		File file = new File("./sandbox/" + dir);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String s = "";
				String data = "0 0";
				while ((s = reader.readLine()) != null) {
					if (s.startsWith("TIE"))
						state = 4;
					if (s.startsWith("WINNER")) {
						state = Integer.parseInt(s.substring(7)) + 5;
					}
					if (s.startsWith("REPORT")){
						data = s.substring(7);
					}
				}
				Socket socket = new Socket(Param.SERVER_IP, Param.MASTERPORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println(Param.TYPE_CLOSE);
				out.println(state + " " + data);
				out.flush();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				in.readLine();
				socket.close();
				reader.close();
			} catch (Exception e) {
				state = 3;
				logger.error("获取结果失败 " + file);
				return false;
			}
		}
		return true;
	}
	
	//处理一次评测
	public boolean DoTask(ArrayList<String> data, int insize, int outsize) {
		// System.out.println(insize + " " + outsize);

		//准备评测环境
		try {
			Process process = Runtime.getRuntime().exec("sudo rm -r sandbox/");
			process.waitFor();
			Process process2 = Runtime.getRuntime().exec("mkdir sandbox");
			process2.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("sandbox准备失败");
			return false;
		}
		
		//准备文件
		for (int i = 0; i < insize; ++i) {// 把文件拷贝到sandbox目录中
			String str[] = data.get(i).split(" ");
			if (!cp("./client/" + str[0], str[0], "./sandbox/" + str[1]))
				return false;
		}
		try {
			Process process = Runtime.getRuntime().exec(
					"make -C ./sandbox/ test");// 执行make
			process.waitFor();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("sandbox准备失败");
			return false;
		}
		
		//上传结果
		String r = null;
		for (int i = insize; i < insize + outsize; ++i) {// 上传结果
			String str[] = data.get(i).split(" ");
			if (r == null)
				r = str[0];
			if (!submit(str[0], str[1]))
				return false;
		}
		if (!report(r))
			return false;
		return true;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(Param.SLAVEPORT);
			logger.debug("slave 端口绑定成功");
		} catch (Exception e) {
			logger.error("slave 端口绑定失败");
			slaveNode.SetStopFlag();
			return;
		}
		try {
			while (!stopFlag) {
				Socket socket = serverSocket.accept();
				logger.debug("获取到任务请求");
				
				//de dao renwu
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String str = in.readLine();
				int insize = Integer.parseInt(str.split(" ")[0]);
				int outsize = Integer.parseInt(str.split(" ")[1]);
				ArrayList<String> data = new ArrayList<String>();
				for (int i = 0; i < insize + outsize; ++i) {
					data.add(in.readLine());
				}
				socket.close();
				
				
				//chu li renwu
				if (!DoTask(data, insize, outsize)) {
					slaveNode.SetStopFlag();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("slave 非法关闭");
			slaveNode.SetStopFlag();
			return;
		}
	}
}
