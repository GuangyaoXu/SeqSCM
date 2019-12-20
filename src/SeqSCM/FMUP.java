package SeqSCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class FMUP {
	private double p;

	private int g;

	private int maxL;

	private final ArrayList<String> t;

	private int Qt;

	private int Rt;

	private double Ut;

	private int symbol;

	private String negativeLabel;

	private String positiveLabel;

	private Inverted it;

	public String maxT;

	public double maxUt;

	public ArrayList<String> PN;

	public FMUP(double p, int g, int maxL, String negativeLabel,
			String positiveLabel) {
		this.p = p;//
		this.g = g;//
		this.maxL = maxL;//
		this.t = new ArrayList<String>();//
		this.Qt = 0;//
		this.Rt = 0;//
		this.Ut = 0;//
		this.symbol = 0;//
		this.negativeLabel = negativeLabel;
		this.positiveLabel = positiveLabel;
		this.it = new Inverted();
		this.maxT = "";//
		this.maxUt = Double.NEGATIVE_INFINITY;//
		this.PN = new ArrayList<String>();//
	}

	public void read(ArrayList<String> trainList,
			ProjectedDataBase projectedDataBase) {
		int documentID = 0;
		for (String line : trainList) {
			String[] str = line.split("\t");
			Sequence sequence = new Sequence();
			String[] itemSet = str[1].split(" ");
			it.buildInvertedIndex(documentID, itemSet);
			sequence.id = documentID;
			documentID++;
			sequence.classLabel = str[0];
			sequence.sequenceData = str[1];
			sequence.completeSequence = line;
			projectedDataBase.dataBase.add(sequence);

			LinkedHashSet<Integer> indexList = new LinkedHashSet<Integer>();

			indexList.add(-1);

			projectedDataBase.indexes.add(indexList);
		}
	}

	public void print_t(ProjectedDataBase projected) throws IOException {
		if (t.size() > 0 && Ut > maxUt) {
			PN.clear();
			maxUt = Ut;
			String pattern = "";
			for (String s : t) {
				pattern = pattern + s + " ";
			}
			maxT = pattern;

			for (int i = 0; i < projected.dataBase.size(); i++) {
				PN.add(projected.dataBase.get(i).completeSequence);
			}
		}
	}

	public void getR(ArrayList<String> trainList) throws IOException {
		ProjectedDataBase projectedDataBase = new ProjectedDataBase();
		this.read(trainList, projectedDataBase);
		fmup(projectedDataBase);
	}

	public void fmup(ProjectedDataBase projected) throws IOException {

		if (symbol != 0 && Qt == 0) {
			return;
		}

		symbol = 1;

		if (Qt <= maxUt) {
			return;
		}

		if (maxL != 0 && t.size() > maxL) {
			return;
		}

		Ut = Qt - p * Rt;
		this.print_t(projected);
		HashSet<String> alphabet = new HashSet<String>();
		List<Sequence> dataBase = projected.dataBase;

		for (int i = 0; i < dataBase.size(); i++) {
			if (dataBase.get(i).classLabel.equals(negativeLabel)) {
				String sequenceData = dataBase.get(i).sequenceData;
				String[] itemSet = sequenceData.split(" ");
				for (int startIndex : projected.indexes.get(i)) {
					int endIndex = 0;
					if (startIndex == -1) {
						startIndex = 0;
						endIndex = itemSet.length;
					} else if (startIndex + g + 1 > itemSet.length) {
						endIndex = itemSet.length;
					} else {
						endIndex = startIndex + g + 1;
					}

					for (int iter = startIndex; iter < endIndex; iter++) {
						alphabet.add(itemSet[iter]);
					}
				}
			}
		}

		ProjectedDataBase projectedDataBase = new ProjectedDataBase();
		List<Sequence> newDataBase = projectedDataBase.dataBase;
		List<LinkedHashSet<Integer>> newIndeces = projectedDataBase.indexes;

		for (String item : alphabet) {
			for (int i = 0; i < dataBase.size(); i++) {
				Map<Integer, ArrayList<Integer>> temp = it.word.get(item);
				ArrayList<Integer> indexList = new ArrayList<Integer>();
				LinkedHashSet<Integer> indexSet = new LinkedHashSet<Integer>();
				int flag = 0;
				if ((indexList = temp.get(dataBase.get(i).id)) != null) {
					for (int startIndex : projected.indexes.get(i)) {
						if (startIndex == -1) {
							flag = 1;
							for (int index : indexList) {
								indexSet.add(index + 1);
							}
						} else {
							for (int index : indexList) {
								if ((index - startIndex) <= g
										&& (index - startIndex) >= 0) {
									flag = 1;
									indexSet.add(index + 1);
								}
							}
						}
					}
				}
				if (flag == 1) {
					newIndeces.add(indexSet);
					newDataBase.add(dataBase.get(i));
				}
			}
			t.add(item);
			int supportP = 0;
			int supportN = 0;
			for (int i = 0; i < projectedDataBase.dataBase.size(); i++) {
				if (projectedDataBase.dataBase.get(i).classLabel
						.equals(positiveLabel)) {
					supportP++;
				} else {
					supportN++;
				}
			}

			Qt = supportN;
			Rt = supportP;
			fmup(projectedDataBase);
			t.remove(t.size() - 1);
			projectedDataBase.clear();
		}

	}
}
