package urlshortener.team.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class SuggestionServiceImpl implements SuggestionService {

	private static final Logger LOG = LoggerFactory.getLogger(SuggestionServiceImpl.class);
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	@Autowired
	private SynonymService synonymService;
	
	private List<String> words;	
	
	public SuggestionServiceImpl() {
		fillDictionary();
	}

	@Override
	public List<String> getSuggestions(String userWord) {

		//List<String> suggestions = words.stream().sorted(Comparator.comparingInt(s -> StringUtils.getLevenshteinDistance(s, userWord))).limit(10).collect(Collectors.toList());

		List<String> similar = new ArrayList<>();
		
		if (!userWord.isEmpty()) {
			for (String word : words) {
				boolean coincidences = true;
				for (int i = 0; i < userWord.length(); i++) {

					if (userWord.length() <= word.length()) {
						if (!userWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {
							coincidences = false;
							break;
						}
					}
				}
				if (coincidences) {
					if (userWord.length() < word.length()) {
						if (uniqueId(word)) {
							similar.add(word);
							if (similar.size() > 3) {
								return similar;
							}
						}					
					}
				}
			}
		}

		// Sort suggestions using Levenshtein's distance algorithm
		similar.sort(Comparator.comparingInt(s -> StringUtils.getLevenshteinDistance(s, userWord)));

		List<String> synonyms = synonymService.getSynonyms(userWord);

		List<String> suggestions = new ArrayList<>();
		suggestions.addAll(similar);
		suggestions.addAll(synonyms);

		return suggestions;
	}
	
	private void fillDictionary(){

		words = new LinkedList<>();
		String file = "src/main/resources/wordsEn.txt";
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while((line = br.readLine()) != null)
				if(line.length() > 0)
					words.add(line);

			fr.close();
		}
		catch(Exception e) {
			LOG.error("Error leyendo fichero "+ file + ": " + e);
		}

	}
	
	private boolean uniqueId(String id) {
		
		ShortURL l = shortURLRepository.findByKey(id);
		
		if (l == null & !id.equals("")) {
            return true;
		}
		
		return false;
	}

}
