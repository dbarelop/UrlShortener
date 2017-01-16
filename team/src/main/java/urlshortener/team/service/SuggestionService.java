package urlshortener.team.service;

import java.util.List;

public interface SuggestionService {
	
	List<String> getSuggestions(String word);
}
