package TrimQuals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputFastq {

	public static void main(String[] args) throws IOException {

		File seq = new File(args[0]);
		File quals = new File(args[1]);
		File out = new File(args[2]);
		
		Map<String, String> seqHash = new HashMap<String,String>();
		Map<String, List<Integer>> qualHash = new HashMap<String,List<Integer>>();
		
		BufferedReader seqReader = new BufferedReader(new FileReader(seq));
		BufferedReader qualReader = new BufferedReader(new FileReader(quals));
		BufferedWriter outBuff = new BufferedWriter(new FileWriter(out)); 
		
		String line;
		String header = "";
		boolean first = true;
		Pattern namePatt = Pattern.compile(">(\\S+)");
		String sequence = "";
		
		while ((line = seqReader.readLine()) != null) {
			
			Matcher nameMatch = namePatt.matcher(line);
			
			if (first == true && nameMatch.matches()) {

				header = nameMatch.group(1);
				first = false;				
				
			} else if (first == false && nameMatch.matches()) {

				seqHash.put(header, sequence);
				sequence = "";
				header = nameMatch.group(1);
				
			} else {
				
				sequence += line;
				
			}
			
		}

		seqHash.put(header, sequence);
		seqReader.close();
		
		first = true;
		header = "";
		String data[];
		List<Integer> qualList = new ArrayList<Integer>();
		namePatt = Pattern.compile(">(\\S+)\\s*");
		
		while ((line = qualReader.readLine()) != null) {
			
			Matcher nameMatch = namePatt.matcher(line);
			
			if (first == true && nameMatch.matches()) {
				
				header = nameMatch.group(1);
				first = false;
				
			} else if (first == false && nameMatch.matches()) {

				qualHash.put(header, qualList);
				qualList = new ArrayList<Integer>();
				header = nameMatch.group(1);
				
			} else {

				Matcher blank = Pattern.compile("\\s*(\\d+[\\S\\s]+)\\s*").matcher(line);
				if (blank.matches()) {
					
					data = blank.group(1).split("\\s+");
					for (String d : data) {

						qualList.add(Integer.parseInt(d));
	
					}
					
				}
				
			}
			
		}
		
		qualHash.put(header, qualList);
		qualReader.close();
		
		for (Map.Entry<String, String> entry : seqHash.entrySet()) {
			
			
			outBuff.write("@" + entry.getKey() + "\n");
			outBuff.write(entry.getValue() + "\n");
			outBuff.write("+" + entry.getKey() + "\n");
			LoadQualities load = new LoadQualities(qualHash.get(entry.getKey()));
			outBuff.write(load.getQualityString() + "\n");
			outBuff.flush();
			
		}
		
		outBuff.close();
		
	}

}
