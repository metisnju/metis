package metis.nju.edu.cn.main;

import metis.nju.edu.cn.master.MasterNode;

import org.apache.log4j.PropertyConfigurator;

public class Master {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("./log4j.properties");
		Thread t1 = new Thread(new MasterNode(), "Master Node");
		t1.start();
	}
}
