package metis.nju.edu.cn.test;

import java.util.ArrayList;
import java.util.TreeSet;

import metis.nju.edu.cn.info.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class logTest {

	/**
	 * @param args
	 */
	static Logger logger = Logger.getLogger(logTest.class);

	// private static Log log = LogFactory.getLog(logTest. class );
	public static void main(String[] args) {
		PropertyConfigurator.configure("./log4j.properties");
		// TODO Auto-generated method stub
		System.out.println("hehe");
		logger.debug("hehrtre");
		TreeSet<TaskInfo> ts = new TreeSet<TaskInfo>();
		ArrayList<TaskInfo> al = new ArrayList<TaskInfo>();
		TaskInfo t = new TaskInfo(0, 0, " ", 0);
		ts.add(t);
		al.add(t);
		++t.id;
		System.out.println(ts.first().id);
		System.out.println(al.get(0).id);
	}

}
