package urlshortener.team.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import urlshortener.team.message.ErrorMessage;
import urlshortener.team.message.SuggestionRequestMessage;
import urlshortener.team.service.SuggestionService;
import urlshortener.team.service.SynonymService;


@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SuggestionController {
	
	private static final Logger logger = LoggerFactory.getLogger(SuggestionController.class);
	
	@Autowired
    private SuggestionService suggestionService;
	
	@Autowired
    private SynonymService synonymService;
	
	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestNames(@RequestParam(value = "shortName", required = false) String shortName) {
		logger.info("Requested suggestionService for short name = " + shortName);
		List<String> s = suggestionService.getSuggestions(shortName);
		
		if (!s.isEmpty()) {
			return new ResponseEntity<>(s, HttpStatus.CREATED);
		} else {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/suggestSynonym", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestSynonym(@RequestParam(value = "shortName", required = false) String shortName){
		logger.info("Requested suggestionService Synonym for short name = " + shortName);
		List<String> s = synonymService.getSynonyms(shortName);
		
		if (!s.isEmpty()) {
			return new ResponseEntity<>(s, HttpStatus.CREATED);
		} else {			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@MessageMapping("/suggestions")
	@SendTo("/topic/suggestions")
	public Object getSuggestionsWebSocket(SuggestionRequestMessage message) {
		logger.info("Requested suggestionService for short name = " + message.getShortName());

		if (message.getShortName() != null) {
			List<String> suggestions = suggestionService.getSuggestions(message.getShortName());
			if (suggestions.isEmpty()) {
				return new ErrorMessage("No suggestions found for " + message.getShortName());
			}
			return suggestions;
		} else {
			return null;
		}

	}
		
}
