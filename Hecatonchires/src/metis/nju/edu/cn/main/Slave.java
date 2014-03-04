package metis.nju.edu.cn.main;

import metis.nju.edu.cn.master.MasterNode;
import metis.nju.edu.cn.slave.SlaveNode;

import org.apache.log4j.PropertyConfigurator;

public class Slave {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("./log4j.properties");
		Thread t1 = new Thread(new SlaveNode(), "SlaveNode");
		t1.start();
	}
}
