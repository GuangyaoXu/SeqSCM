package SeqSCM;

import java.util.ArrayList;
import java.util.BitSet;

public class Classification {
	public Boolean classify(ArrayList<String> R, String testInstance, int g) {
		int ht[] = new int[R.size()];
		for (int x = 0; x < R.size(); x++) {
			ht[x] = 1 - tranwithGap(testInstance, R.get(x), g);
			if (ht[x] == 0) {
				return true;
			}
		}
		return false;
	}

	public int tranwithGap(String s1, String s2, int g) {
		String[] Long = s1.split(" ");
		String[] Short = s2.split(" ");
		if (Short.length == 1) {
			for (String s : Long) {
				if (Short[0].equals(s)) {
					return 1;
				}
			}
			return 0;
		} else {
			BitSet[] bsArray = new BitSet[Short.length];
			int count;
			for (int i = 0; i < Short.length; i++) {
				BitSet bs = new BitSet(Long.length);
				for (int j = 0; j < Long.length; j++) {
					if (Short[i].equals(Long[j])) {
						bs.set(j);
					}
				}
				if (bs.isEmpty()) {
					return 0;
				}
				bsArray[i] = bs;
			}
			count = Short.length;
			while (count > 1) {
				BitSet bs = bsArray[0];
				BitSet[] midArray = new BitSet[g + 1];
				for (int i = 0; i < g + 1; i++) {
					midArray[i] = move(bs, Long.length);
					bs = move(bs, Long.length);
				}

				bs = midArray[0];
				for (int i = 0; i < midArray.length; i++) {
					bs.or(midArray[i]);
				}
				bs.and(bsArray[1]);
				bsArray[0] = bs;
				for (int i = 1; i < bsArray.length - 1; i++) {
					bsArray[i] = bsArray[i + 1];
				}
				count--;
			}
			if (bsArray[0].isEmpty()) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	public BitSet move(BitSet bs, int size) {
		int[] array = new int[size];
		for (int i = 0; i < array.length - 1; i++) {
			if (bs.get(i)) {
				array[i + 1] = 1;
			}
		}
		BitSet newBs = new BitSet(array.length);
		for (int i = 0; i < array.length; i++) {
			if (array[i] == 1) {
				newBs.set(i);
			}
		}
		return newBs;
	}
}
