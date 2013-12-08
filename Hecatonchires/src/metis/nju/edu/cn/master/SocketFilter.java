package metis.nju.edu.cn.master;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.Statement;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.info.SlaveFactory;
import metis.nju.edu.cn.info.SlaveInfo;
import metis.nju.edu.cn.info.TaskInfo;

import org.apache.log4j.Logger;

public class SocketFilter implements Runnable {
	Socket socket;
	SlaveFactory slaveFactory;
	MasterNode masterNode;
	static Logger logger = Logger.getLogger(SocketFilter.class);
	boolean stopFlag;

	public SocketFilter(Socket s, SlaveFactory sf, MasterNode m) {
		masterNode = m;
		slaveFactory = sf;
		socket = s;
		stopFlag = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			int type = Integer.parseInt(in.readLine());
			System.out.println(type);
			
			if (type == Param.TYPE_REGISTER) {//注册
				logger.debug(socket.getInetAddress() + "register");
				String id = in.readLine();
				String password = in.readLine();
				String ip = socket.getInetAddress().toString();
				System.out.println(id + " " + password + " " + ip);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				if (slaveFactory.register(ip, id, password))
					out.println("OK");
				else
					out.println("Error");
				out.flush();
				Thread.sleep(50);
				socket.close();

			} else if (type == Param.TYPE_HEARTBEAT) {//心跳
				logger.debug(socket.getInetAddress() + "heartbeat");
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				String ip = socket.getInetAddress().toString();
				if (slaveFactory.heartBeat(ip))
					out.println("OK");
				else
					out.println("Error");
			} else if (type == Param.TYPE_GETFILE) {// 请求文件
				logger.debug(socket.getInetAddress() + "getfile");
				File file = new File(Param.DIR_SERVER + in.readLine());
				/*
				 * PrintWriter out = new PrintWriter(socket.getOutputStream(),
				 * true); if (file.exists()){ BufferedReader reader =new
				 * BufferedReader(new FileReader(file)); String s="";
				 * while((s=reader.readLine())!=null){ out.write(s+"1\n");
				 * out.flush(); } out.write("0\n"); out.flush(); reader.close();
				 * }else{ out.write("0\n"); out.flush();
				 * logger.error(socket.getInetAddress() + " 请求的文件：" + file +
				 * "不存在"); } out.flush(); Thread.sleep(50); socket.close();
				 */
				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];
				try {
					DataInputStream fis = new DataInputStream(
							new BufferedInputStream(new FileInputStream(file)));
					DataOutputStream dos = new DataOutputStream(
							socket.getOutputStream());

					// dos.writeUTF(file.getName());
					// dos.flush();
					// dos.writeLong(file.length());
					// dos.flush();

					int read = 0;
					int passedlen = 0;
					long length = file.length(); // 获得要发送文件的长度
					while ((read = fis.read(buf)) != -1) {
						passedlen += read;
						System.out.println("已经完成文件 [" + file.getName()
								+ "]百分比: " + passedlen * 100L / length + "%");
						dos.write(buf, 0, read);
					}

					dos.flush();
					fis.close();
					dos.close();
					socket.close();
					System.out.println("文件 " + file + "传输完成!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == Param.TYPE_SUBMIT) {
				logger.debug(socket.getInetAddress() + "submit");
				File file = new File(Param.DIR_ANSWER + in.readLine());
				File parent = file.getParentFile();
				if ((parent != null) && !parent.exists())
					parent.mkdirs();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				String str;
				while ((str = in.readLine()).endsWith("1")) {
					writer.write(str.substring(0, str.length() - 1));
					writer.newLine();
				}
				writer.close();
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println("OK");
				out.flush();
				Thread.sleep(50);
				socket.close();
			}else if (type == Param.TYPE_CLOSE){//评测结束
				logger.debug(socket.getInetAddress() + "close");
				String data = in.readLine();
				String ip = socket.getInetAddress().toString();
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println("OK");
				out.flush();
				Thread.sleep(50);
				socket.close();
				System.out.println(data);
				SlaveInfo slaveInfo = slaveFactory.getSlaveByIp(ip);
				report(slaveInfo, data);
				slaveFactory.setUnwork(ip);
				//待添加
			}
			logger.debug("Master读取socket完成");

		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println(e.getStackTrace());
			// TODO Auto-generated catch block
			logger.error("Master读取socket失败");
		}
	}

	private void report(SlaveInfo slaveInfo, String data) {
		// TODO Auto-generated method stub
		//System.out.println(slaveInfo.taskInfo.id);
		try{
			String []s = data.split(" ");
			Connection con = Param.getConnection();
			String query = "UPDATE tbattle " +
					"SET status=" + s[0] + ",video_url='out.txt',judge_id=0,PID1_score=" + s[1] + ",PID2_score=" + s[2]  +
					" WHERE BID=" + slaveInfo.taskInfo.id;
			System.out.println(query);

			Statement st = con.createStatement();
			if (!st.execute(query)){
				masterNode.SetStopFlag();
				
			}
			st.close();
			con.close();
		}catch(Exception e){
			logger.error("insert error");
			return;
		}
	}

}
