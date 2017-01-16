package urlshortener.team.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.util.ResourceUtils;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class SuggestionServiceImpl implements SuggestionService {

	private static final Logger logger = LoggerFactory.getLogger(SuggestionServiceImpl.class);

	private File dictionary;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	@Autowired
	private SynonymService synonymService;
	
	private List<String> words;	
	
	public SuggestionServiceImpl() throws IOException {
		dictionary = ResourceUtils.getFile("classpath:wordsEn.txt");
		fillDictionary();
	}

	@Override
	public List<String> getSuggestions(String userWord) {

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
						if (shortURLRepository.findByKey(word) == null) {
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

		return suggestions.stream().filter(s -> shortURLRepository.findByKey(s) == null).collect(Collectors.toList());
	}
	
	private void fillDictionary() throws IOException {
		words = new ArrayList<>();
		try (FileReader fr = new FileReader(dictionary)) {
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null)
				words.add(line);
		}
	}

}
