package urlshortener.team.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Service
public class SuggestServiceImpl implements SuggestService {

	private static final Logger LOG = LoggerFactory.getLogger(SuggestServiceImpl.class);
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private List<String> words;	
	
	public SuggestServiceImpl() {
		fillDictionary();
	}

	@Override
	public List<String> suggest(String userWord) {

		List<String> suggest = new LinkedList<String>();
		
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
							suggest.add(word);
							if (suggest.size() > 3) {
								return suggest;
							}
						}					
					}
				}
			}
		}
		return suggest;
	}
	
	private void fillDictionary(){

		words = new LinkedList<String>();
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
