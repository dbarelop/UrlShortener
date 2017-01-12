package urlshortener.team.service;

import java.util.List;

public interface SuggestService {
	
	List<String> suggest(String word);
}
