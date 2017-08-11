package TrimQuals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

public class LoadQualities {

	private static Hashtable<Integer,Character> quals;
	private static List<Integer> toCompute;
	
	public LoadQualities(List<Integer> toCompute) throws IOException {
		quals = loadQualities();
		LoadQualities.toCompute = toCompute;
	}
	private Hashtable<Integer,Character> loadQualities() throws IOException {
		
		BufferedReader qualityStream;
		qualityStream = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/importFiles/qualities.txt"), "UTF-8"));
		
		Hashtable<Integer,Character> qualityTable = new Hashtable<Integer,Character>();
		String myLine;
		String data[];
		
		try {
			while((myLine = qualityStream.readLine()) != null) {
				data = myLine.split("\t");
				qualityTable.put(Integer.parseInt(data[1]), data[0].charAt(0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		qualityStream.close();
		return qualityTable;
		
	}
	public String getQualityString() {
		
		String quality = "";
		
		for (Integer i : toCompute) {
			quality += quals.get(i);
		}

		return quality;
		
	}
}
