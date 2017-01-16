package urlshortener.team.service;

import java.util.List;

public interface SynonymService {
	
	List<String> getSynonyms(String word);
}
