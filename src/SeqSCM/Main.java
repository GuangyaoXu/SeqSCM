package SeqSCM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws IOException {
		// start time
		long startTime = System.currentTimeMillis();
		Main m = new Main();
		// data set
		String datasetName = "aslbu";
		// penalty parameter
		double p = 2;
		// gap constraint
		int g = 1;
		// maximum length
		int maxL = 4;
		// early stopping point
		int a = 1;

		System.out.println("Data set: " + datasetName + ", p: " + p + ", g: "
				+ g + ", maxL: " + maxL + ", a: " + a);

		// training set
		ReadFile rf = new ReadFile("./aslbuTrain.txt");
		ArrayList<String> trainList = rf.getFileList();

		// store the different class labels and the corresponding sequences
		Map<String, ArrayList<String>> trainMap = new LinkedHashMap<String, ArrayList<String>>();
		for (String line : trainList) {
			String[] str = line.split("\t");
			if (trainMap.containsKey(str[0])) {
				trainMap.get(str[0]).add(line);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(line);
				trainMap.put(str[0], list);
			}
		}
		m.multiClassOneVsOne(g, maxL, p, a, trainMap);

		// end time
		long endTime = System.currentTimeMillis();
		System.out.println("The running time£º" + (endTime - startTime) + "ms");
	}

	public void multiClassOneVsOne(int g, int maxL, double p, int a,
			Map<String, ArrayList<String>> trainMap) throws IOException {
		String classArray[] = new String[trainMap.size()];
		ArrayList<ArrayList<String>> classList = new ArrayList<ArrayList<String>>();
		int index = 0;
		for (Map.Entry<String, ArrayList<String>> e : trainMap.entrySet()) {
			classArray[index++] = e.getKey();
			classList.add(e.getValue());
		}

		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		double sum = 0;
		int count = 0;
		// construct k(k-1)/2 classifiers
		String label = null;
		for (int m = 0; m < classArray.length - 1; m++) {
			for (int n = m + 1; n < classArray.length; n++) {
				Method mt = new Method();
				count++;
				label = m + " " + n;
				System.out.println(count + " Negative Class: " + classArray[m]
						+ " Positive Class: " + classArray[n]);
				ArrayList<String> R = mt.SeqSCM(classList.get(m),
						classList.get(n), p, a, g, maxL, classArray[m],
						classArray[n]);
				sum += R.size();
				map.put(label, R);
			}
		}

		ReadFile rf = new ReadFile("./aslbuTest.txt");
		ArrayList<String> testList = rf.getFileList();

		Classification cf = new Classification();
		int correct = 0;

		System.out.println();
		for (String testInstance : testList) {
			System.out.println(testInstance);
			String strTest[] = testInstance.split("\t");
			int[] vote = new int[classArray.length];
			for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {

				String[] labelArray = entry.getKey().split(" ");

				ArrayList<String> R = entry.getValue();
				Boolean isNegativeClass = cf.classify(R, strTest[1], g);
				if (isNegativeClass) {
					vote[Integer.parseInt(labelArray[0])]++;
				} else {
					vote[Integer.parseInt(labelArray[1])]++;
				}
			}
			int max = 0;
			int key = 0;
			System.out.print("Class: ");
			for (int k = 0; k < vote.length; k++) {
				System.out.print(classArray[k] + ":" + vote[k] + " ");
				if (vote[k] > max) {
					max = vote[k];
					key = k;
				}
			}
			System.out.println();
			System.out.println("Assign label: " + classArray[key]
					+ ", True label: " + strTest[0]);
			if (classArray[key].equals(strTest[0])) {
				correct++;
			}
			System.out.println();
		}
		System.out.println("The number of average rule: "
				+ String.format("%.3f", sum / count));
		System.out.println("Accuracy: "
				+ String.format("%.3f", correct * 1.0 / testList.size()) + " ("
				+ correct + "/" + testList.size() + ")");

	}
}
