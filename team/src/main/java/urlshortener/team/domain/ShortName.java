package urlshortener.team.domain;

import java.util.List;

public class ShortName{
	
	private String hash;
	private List<String> suggestHash;
	private List<String> Dictionary;
	
	public ShortName(String hash) {
		this.hash = hash;
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
	
}