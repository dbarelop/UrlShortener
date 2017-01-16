package urlshortener.team.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import urlshortener.team.repository.ShortURLRepository;

@Service
public class SynonymServiceImpl implements SynonymService {

	private static final Logger logger = LoggerFactory.getLogger(SynonymServiceImpl.class);
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	@Override
	public List<String> getSynonyms(String userWord) {

		List<String> suggestSynonym  = new LinkedList<String>();
		
		if (userWord.length()>2) {

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
				logger.info("response code: " + responseCode);

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

						int index = 4;
						if (fromJson.size() < 4) {
							index = fromJson.size();
						}

						for (int i = 0; i < index; i++) {
							if (shortURLRepository.findByKey(fromJson.get(i)) == null) {
								suggestSynonym.add(fromJson.get(i));
							}
						}
						logger.info("list of synonyms: " + suggestSynonym.toString());
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return suggestSynonym;
	}

}
