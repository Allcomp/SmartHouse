package cz.allcomp.shs.util;
import java.util.HashMap;

public class StringEncDec {

	private static HashMap<String, String> characters;
	
	static {
		characters = new HashMap<>();
		characters.put("á", "?chid=0?");
		characters.put("Á", "?chid=0L?");
		characters.put("č", "?chid=1?");
		characters.put("Č", "?chid=1L?");
		characters.put("ď", "?chid=2?");
		characters.put("Ď", "?chid=2L?");
		characters.put("ě", "?chid=3?");
		characters.put("Ě", "?chid=3L?");
		characters.put("é", "?chid=4?");
		characters.put("É", "?chid=4L?");
		characters.put("í", "?chid=5?");
		characters.put("Í", "?chid=5L?");
		characters.put("ň", "?chid=6?");
		characters.put("Ň", "?chid=6L?");
		characters.put("ó", "?chid=7?");
		characters.put("Ó", "?chid=7L?");
		characters.put("ř", "?chid=8?");
		characters.put("Ř", "?chid=8L?");
		characters.put("š", "?chid=9?");
		characters.put("Š", "?chid=9L?");
		characters.put("ť", "?chid=10?");
		characters.put("Ť", "?chid=10L?");
		characters.put("ú", "?chid=11?");
		characters.put("Ú", "?chid=11L?");
		characters.put("ů", "?chid=12?");
		characters.put("Ů", "?chid=12L?");
		characters.put("ž", "?chid=13?");
		characters.put("Ž", "?chid=13L?");
		characters.put("ý", "?chid=14?");
		characters.put("Ý", "?chid=14L?");
	}
	
	public static String encode(String str) {
		for(String ch : characters.keySet())
			str = str.replace(ch, characters.get(ch));
		return str;
	}
	
	public static String decode(String str) {
		for(String ch : characters.keySet())
			str = str.replace(characters.get(ch), ch);
		return str;
	}
}
