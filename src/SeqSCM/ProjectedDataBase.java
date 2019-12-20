package SeqSCM;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ProjectedDataBase {
	public ArrayList<Sequence> dataBase;

	public List<LinkedHashSet<Integer>> indexes;

	public ProjectedDataBase() {
		this.dataBase = new ArrayList<Sequence>();
		this.indexes = new ArrayList<LinkedHashSet<Integer>>();
	}

	public void clear() {
		this.dataBase.clear();
		this.indexes.clear();
	}
}
