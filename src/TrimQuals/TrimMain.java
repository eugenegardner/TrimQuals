package TrimQuals;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrimMain {
	
	public static void main(String[] args) throws IOException {

		File seq = new File(args[0]);
		File quals = new File(args[1]);
		File out = new File(args[2]);
		int trimQual = Integer.parseInt(args[3]);
		
		Map<String, String> seqHash = new HashMap<String,String>();
		Map<String, List<Integer>> qualHash = new HashMap<String,List<Integer>>();
		
		BufferedReader seqReader = new BufferedReader(new FileReader(seq));
		BufferedReader qualReader = new BufferedReader(new FileReader(quals));
		BufferedWriter outBuff = new BufferedWriter(new FileWriter(out)); 
		
		String line;
		String header = "";
		boolean first = true;
		Pattern namePatt = Pattern.compile(">\\s*(\\S+)\\s*");
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

			Sequences trimmedSeq = getTrimmedSeq(entry.getValue(), qualHash.get(entry.getKey()), trimQual);
			
			if (trimmedSeq.getDna().length() > 50) {
			
				outBuff.write("@" + entry.getKey() + "\n");
				outBuff.write(trimmedSeq.getDna() + "\n");
				outBuff.write("+" + entry.getKey() + "\n");
				LoadQualities load = new LoadQualities(trimmedSeq.getQuality());
				outBuff.write(load.getQualityString() + "\n");
				outBuff.flush();
				
			} else {
				
				System.out.println(entry.getKey() + " Has trimmed length shorter than 50bp!");
				
			}
			
		}
		
		outBuff.close();
		
	}

	private static Sequences getTrimmedSeq(String sequence, List<Integer> quals, int trimQual) {
		
		String trimmed = null;
		List<Integer> trimQuals = new ArrayList<Integer>(); 
		
		int inARow = 0;
		int firstPass = 0;
		
		int frontPos = 0;
		int backPos = sequence.length();
		
		// Test front
		
		for (int x = 0; x < quals.size(); x++) {

			if (quals.get(x) <= trimQual) {
				firstPass = 0;
				inARow = 0;
				continue;
			} else {
				if (inARow > 4) {
					frontPos = firstPass;
					break;
				} else {
					if (inARow == 0) {
						firstPass = x;
						inARow++;
					} else {
						inARow++;
					}
				}
			}
		}
			
		// Test back
		
		firstPass = quals.size();
		inARow = 0;
		
		for (int x = quals.size() - 1; x >= 0; x--) {

			if (quals.get(x) <= trimQual) {
				firstPass = 0;
				inARow = 0;
				continue;
			} else {
				if (inARow > 4) {
					backPos = firstPass;
					break;
				} else {
					if (inARow == 0) {
						firstPass = x;
						inARow++;
					} else {
						inARow++;
					}
				}
			}
		}
		
		trimmed = sequence.substring(0, backPos);
		trimmed = trimmed.substring(frontPos, trimmed.length());
		
		for (int x = frontPos; x < backPos; x++) {
			trimQuals.add(quals.get(x));
		}
		
		Sequences seq = new Sequences(trimmed, trimQuals);
		
		return seq;
		
	}
	
	
	
}
