package SeqSCM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {

	private BufferedReader br = null;

	public ReadFile(String filePath) {
		try {
			this.br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getFileList() {
		try {
			String str = null;
			ArrayList<String> list = new ArrayList<String>();
			while ((str = this.br.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (this.br != null) {
					this.br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
