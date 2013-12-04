package metis.nju.edu.cn.master;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
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
	static  Logger logger  =  Logger.getLogger(TaskProducer. class);

	public void SetStopFlag(){
		StopFlag = true;
	}
	public TaskProducer(MasterNode m, SlaveFactory s, TaskFactory t){
		StopFlag = false;
		slaveFactory = s;
		taskFactory = t;
		masterNode = m;
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
	private void GetTaskFromDB(Stack<TaskInfo> tasks, int need) {//从数据库读取数据
		// TODO Auto-generated method stub
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
		}
		first = false;
		/*
		String s = "";
		s += "2 1\n";
		s += "file/a.cpp a.cpp\n";
		s += "file/Makefile Makefile\n";
		s+= "output\n";
		TaskInfo t = new TaskInfo(0, 0, s, 0);
		for (int i = 0; i < need; ++i){
			tasks.add(t);
		
		}
		*/ 
	} 

}
