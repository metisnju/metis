package metis.nju.edu.cn.test;

import metis.nju.edu.cn.info.SlaveFactory;
import metis.nju.edu.cn.master.MasterListener;
import metis.nju.edu.cn.master.MasterNode;
import metis.nju.edu.cn.slave.SlaveNode;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class test {

	static Logger logger = Logger.getLogger(test.class);

	// private static Log log = LogFactory.getLog(logTest. class );
	public static void main(String[] args) {
		PropertyConfigurator.configure("./log4j.properties");

		Thread masterNode = new Thread(new MasterNode(), "MasterNode");
		masterNode.start();
		Thread slaveNode = new Thread(new SlaveNode(), "SlaveNode");
		slaveNode.start();
	}
}
