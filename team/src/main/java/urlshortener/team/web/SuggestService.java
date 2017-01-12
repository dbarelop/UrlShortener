package urlshortener.team.web;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@RestController
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SuggestService {
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger LOG = LoggerFactory.getLogger(SuggestService.class);
	private List<String> words;
	private List<String> suggest;
	private List<String> suggestSynonym;
	
	public SuggestService(){
		fillDictionary();
	}
		
	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestNames(@RequestParam(value = "shortName", 
														required = false) String id) {
		LOG.info("Requested suggest for short name = " + id);
		if (findSuggest(id)) {
			return new ResponseEntity<>(suggest, HttpStatus.CREATED);
		} else {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
		
	private boolean findSuggest(String userWord) {

		if (userWord.isEmpty()) {
			return false;
		}

		boolean suggestionAdded = false;
		suggest = new LinkedList<String>();
		
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
						suggestionAdded = true;
						if (suggest.size() > 5) {
							return true;
						}
					}					
				}
			}
		}
		return suggestionAdded;
	}
	
	@RequestMapping(value = "/suggestSynonym", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestSynonym(@RequestParam(value = "shortName", 
														required = false) String userWord){
		LOG.info("Requested suggest Synonym for short name = " + userWord);
		if (synonymos(userWord)) {
			return new ResponseEntity<>(suggestSynonym, HttpStatus.CREATED);
		} else {			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	private boolean synonymos(String userWord) {

		if (userWord.isEmpty()) {
			return false;
		}

		boolean suggestionAdded = false;
		suggestSynonym = new LinkedList<String>();
		
		//API credentials 
		String X_Mashape_Key = "mashape-key=YEJqDhxdT6mshmh7zzc6Kw42nBjpp1YGIQGjsnerzCX5Cw3rW1";
		// 'X-Mashape-Key: m4oRqAPxyrmsh5rLgjCpk6cwSYLQp1W3fKpjsnmNxXBisWkfIO'
		String detail = "/synonyms?";
		
		//URI for get petition
		String url = "https://wordsapiv1.p.mashape.com/words/"+ userWord + detail + X_Mashape_Key;

		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			int responseCode = con.getResponseCode();
			LOG.info("response code: " + responseCode);
			
			if (responseCode == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);							
				}
				in.close();
				
				//extraction of synonyms of response
				String synonyms = response.toString();			 
				int inicio = synonyms.indexOf("[");
				int fin =synonyms.indexOf("]");
				synonyms = synonyms.substring(inicio, fin+1);

				// Convert synonyms response to List
				Gson gson = new Gson();
				Type listType = new TypeToken<LinkedList<String>>(){}.getType();
				LinkedList<String> fromJson = gson.fromJson(synonyms, listType);

				if (!fromJson.isEmpty()) {
					
					int index = 6;
					
					if (fromJson.size() < 6) {
						index = fromJson.size();
					}
					
					for (int i = 0; i < index; i++) {
						if (uniqueId(fromJson.get(i))) {
							suggestSynonym.add(fromJson.get(i));
							suggestionAdded = true;
						}
					}
					LOG.info("list of synonyms: " + suggestSynonym.toString());
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return suggestionAdded;
	}
	
	private boolean uniqueId(String id) {
		
		ShortURL l = shortURLRepository.findByKey(id);
		
		if (l == null & !id.equals("")) {
            return true;
		}
		
		return false;
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

	
}