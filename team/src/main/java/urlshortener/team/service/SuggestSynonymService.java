package urlshortener.team.service;

import java.util.List;

public interface SuggestSynonymService {
	
	List<String> suggestSynonym(String word);
}
