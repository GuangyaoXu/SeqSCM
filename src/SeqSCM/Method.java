package SeqSCM;

import java.io.IOException;
import java.util.ArrayList;

public class Method {
	public ArrayList<String> SeqSCM(ArrayList<String> negativeSet,
			ArrayList<String> positiveSet, double p, int a, int g, int maxL,
			String negativeLabel, String positiveLabel) throws IOException {

		ArrayList<String> R = new ArrayList<String>();

		ArrayList<String> trainList = new ArrayList<String>();

		trainList.addAll(negativeSet);
		trainList.addAll(positiveSet);

		while (true) {
			FMUP f = new FMUP(p, g, maxL, negativeLabel, positiveLabel);
			f.getR(trainList);
			if (f.maxUt <= a) {
				break;
			}
			R.add(f.maxT);
			System.out.println("Rule: " + f.maxT);
			trainList.removeAll(f.PN);
			int flag = 0;
			for (String trainInstance : trainList) {
				String trainSet[] = trainInstance.split("\t");
				if (trainSet[0].equals(negativeLabel)) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				break;
			}
		}
		return R;
	}
}
