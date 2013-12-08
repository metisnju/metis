package metis.nju.edu.cn.info;

import metis.nju.edu.cn.info.SlaveFactory.Order;

public class TaskInfo implements Comparable {
	public String data;
	public int level;
	public int index;
	public int id;

	public TaskInfo(int Level, int Id, String Data, int Index) {
		id = Id;
		level = Level;
		index = Index;
		data = Data;
	}

	public boolean equals(Object obj) {
		TaskInfo b = (TaskInfo) obj;
		return (level == b.level) && (index == b.index);
	}

	@Override
	public int compareTo(Object arg0) {
		TaskInfo b = (TaskInfo) arg0;
		// System.out.println(this.level + " " + b.level + " " + this.index +
		// " " + b.index);
		if (level != b.level)
			return b.level - level;
		return index - b.index;
	}

	public TaskInfo clone() {
		return new TaskInfo(level, id, data, index);
	}
}
