package urlshortener.team.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.team.domain.ShortName;

@RestController
public class ShortNameController {
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
		
	private List<String> words;
	private ShortName shortname;
	
	public ShortNameController(){
		shortname  = new ShortName();
		words = shortname.getDictionary();
	}
	
	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	@RequestMapping(value = "/brandedLink", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortenerid(@RequestParam("url") String url,
											  @RequestParam(value = "shortName", required = false) String id,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request) {

		ShortURL su = createAndSaveIfValid(id,url, sponsor, UUID
				.randomUUID().toString(), extractIP(request));
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	private ShortURL createAndSaveIfValid(String id, String url, String sponsor, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

		if (urlValidator.isValid(url)) {

			String finalId;
			ShortURL l = shortURLRepository.findByKey(id);			
			List<ShortURL>  ListUrl = shortURLRepository.findByTarget(url);
			
			if (!(ListUrl.isEmpty())) {				
				shortURLRepository.delete(ListUrl.get(0).getHash());
			}
						
			if (l == null & !id.equals("")) {
				
				finalId = id;
				suggest(id);
								
				ShortURL su = new ShortURL(finalId, url,
						linkTo(methodOn(UrlShortenerController.class).redirectTo(finalId, null)).toUri(), sponsor,
						new Date(System.currentTimeMillis()), owner, HttpStatus.TEMPORARY_REDIRECT.value(), true, ip,
						null);
				return shortURLRepository.save(su);

			} else {
				System.out.println("shortName already exist " + id);
				return null;
			}

		} else {
			System.out.println("link invalid");
			return null;
		}

	}
		
	
	private boolean suggest(String UserWord) {

		if (UserWord.isEmpty()) {
			return false;
		}
		System.out.println("User word: " + UserWord);
		boolean suggestionAdded = false;

		for (String word : words) {
			boolean coincidences = true;
			for (int i = 0; i < UserWord.length(); i++) {

				if (UserWord.length() <= word.length()) {
					if (!UserWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {
						coincidences = false;
						break;
					}
				}
			}
			if (coincidences) {
				if (UserWord.length() < word.length()) {
					addSuggestions(word);
					suggestionAdded = true;
				}
			}
		}
		return suggestionAdded;
	}

	private void addSuggestions(String word) {
		System.out.println("suggest: " + word);
		//TODO implementar la respuesta de las sugerencias en el cliente
	}
	
	
	
	
	
}