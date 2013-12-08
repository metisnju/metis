package metis.nju.edu.cn.master;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Stack;

import metis.nju.edu.cn.info.Param;
import metis.nju.edu.cn.info.SlaveFactory;
import metis.nju.edu.cn.info.TaskFactory;
import metis.nju.edu.cn.info.TaskInfo;

import org.apache.log4j.Logger;

public class TaskProducer implements Runnable  {
	static boolean first = true;
	MasterNode masterNode;
	SlaveFactory slaveFactory;
	TaskFactory taskFactory;
	boolean StopFlag;
	Connection con;
	static  Logger logger  =  Logger.getLogger(TaskProducer. class);

	public void SetStopFlag(){
		if (con != null){
			try{
				con.close();	
			}catch (Exception e){
				logger.warn("数据库关闭失败");
			}
		}
		StopFlag = true;
	}
	public TaskProducer(MasterNode m, SlaveFactory s, TaskFactory t){
		StopFlag = false;
		slaveFactory = s;
		taskFactory = t;
		masterNode = m;
		con = Param.getConnection();
		if (con == null){
			logger.error("简历数据库连接失败");
			m.SetStopFlag();
		}
	}
	@Override
	public void run() {
		Stack<TaskInfo> tasks = new Stack<TaskInfo>();
		TaskInfo taskInfo = null;
		while(!StopFlag){
			slaveFactory.recycle(tasks);//回收没有成功完成的task
			while (!tasks.empty()){//插入taskFactory
				taskInfo = tasks.pop();
				if (taskFactory.insert(taskInfo)) continue;
				tasks.push(taskInfo);
				break;
			}
			if (tasks.isEmpty()){//如果为空，说明taskFactory还有空位,从数据库读取新数据
				GetTaskFromDB(tasks, taskFactory.getNeedSize());
				while (!tasks.empty()){//插入taskFactory
					taskInfo = tasks.pop();
					if (taskFactory.insert(taskInfo)) continue;
					tasks.push(taskInfo);
					break;
				}
			}
			try{
				Thread.sleep(Param.SLEEP_Task_PRODUCER);
			}catch(Exception e){
				logger.error("睡眠异常");
			}
		}
	}
	private boolean clear(){//清空所有评测中的评测
		try{
			Statement st = con.createStatement();
			String query = "UPDATE tbattle SET STATUS=0 WHERE STATUS=1";
			if (!st.execute(query)){
				masterNode.SetStopFlag();
				
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
	private void GetTaskFromDB(Stack<TaskInfo> tasks, int need) {//从数据库读取数据
		if (first){
			first = false;
			if (!clear()){
				return ;
			}
		}
		// TODO Auto-generated method stub
		/*
		if (!first) return;
		String s="";
		String ans = "";
		ArrayList<String> al = new ArrayList<String>();
		try {
			BufferedReader reader;
			reader = new BufferedReader(new FileReader("input"));
			while((s=reader.readLine())!=null){
				
				ans = s + '\n';
				for (int i = 0; i < 6; ++i){
					s = reader.readLine();
					ans += s + '\n';
				}
				al.add(ans);
			}
			//System.out.print(ans);
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < al.size(); ++i){
			TaskInfo t = new TaskInfo(0, 0, al.get(i), 0);
			tasks.add(t);
		}*/
		try{
    	String query = "SELECT BID, tbattle.CID, tbattle.level, video_url," +
    			" tprog1.url as url1, tprog1.UID as UID1," +
    			" tprog2.url as url2, tprog2.UID as UID2 " +
    			"FROM tbattle, tprog as tprog1, tprog as tprog2 " +
    			"where tbattle.valid=1 and status=0 and PID1=tprog1.PID and PID2=tprog2.PID " +
    			"limit " + String.valueOf(need);
    	
    	Statement st = con.createStatement();
    	ResultSet rs = st.executeQuery(query);
    	while (rs.next()){
    		String file1 = rs.getString("CID") + "/judge.out";
    		String file2 = rs.getString("CID") + "/Makefile";
    		String file3 = rs.getString("CID") + "/" + rs.getString("UID1") + "/" + rs.getString("url1") + ".out";
    		String file4 = rs.getString("CID") + "/" + rs.getString("UID2") + "/" + rs.getString("url2") + ".out";
    		String file5 = rs.getString("BID") + "/" + rs.getString("video_url");
    		String data = "5 1\n" + 
    				file1 + " judge/program\n" +
    				file2 + " Makefile\n" + 
    				file3 + " ai0/program\n" + 
    				file4 + " ai1/program\n" + 
    				"themis.out themis.out\n" + 
    				"output " + file5;
    		int id = rs.getInt("BID");
    		int level = rs.getInt("level");
    		System.out.println(data);
    		TaskInfo taskInfo = new TaskInfo(level, id, data, 0);
    		tasks.add(taskInfo);
    		{
    			Statement st2 = con.createStatement();
    			String query2 = "UPDATE tbattle SET STATUS =1 WHERE BID=" + rs.getString("BID");
    			if (!st2.execute(query2)){
    				masterNode.SetStopFlag();
    				
    			}
    			st2.close();
    		}
    	}
    	rs.close();
    	st.close();
		}catch (Exception e){
			masterNode.SetStopFlag();
			logger.error("数据库查询失败");
			logger.error(e.getMessage());
		}
	} 

}
