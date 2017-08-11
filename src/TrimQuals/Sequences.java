package TrimQuals;

import java.util.List;

public class Sequences {

	private static List<Integer> quality;
	private static String dna;
	public Sequences (String dna, List<Integer> quality) {
		
		Sequences.quality = quality;
		Sequences.dna = dna;
		
	}
	
	public List<Integer> getQuality() {
		return quality;
	}
	public String getDna() {
		return dna;
	}
	
}
