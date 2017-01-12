package urlshortener.team.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class ShortName{
	
	private String hash;
	private List<String> suggestHash;
	private List<String> Dictionary;
	
	public ShortName() {
		Dictionary = new LinkedList<>();
		fillDictionary(Dictionary);
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public List<String> getSuggestHash() {
		return suggestHash;
	}

	public void setSuggestHash(List<String> suggestHash) {
		this.suggestHash = suggestHash;
	}

	public List<String> getDictionary() {
		return Dictionary;
	}

	public void setDictionary(List<String> dictionary) {
		Dictionary = dictionary;
	}
	
	private void fillDictionary(List<String> words){

		String file = "src/main/resources/wordsEn.txt";
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String linea;
			while((linea = br.readLine()) != null)
				if(linea!=null && linea.length()>0)        	
					words.add(linea);

			fr.close();
		}
		catch(Exception e) {
			System.out.println("Error leyendo fichero "+ file + ": " + e);
		}

	}
	
}