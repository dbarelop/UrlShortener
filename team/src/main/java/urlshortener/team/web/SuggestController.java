package urlshortener.team.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import urlshortener.team.message.SuggestRequestMessage;
import urlshortener.team.service.SuggestService;
import urlshortener.team.service.SuggestSynonymService;


@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SuggestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SuggestController.class);
	
	@Autowired
    private SuggestService suggest;
	
	@Autowired
    private SuggestSynonymService suggestSynonym;
	
	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestNames(@RequestParam(value = "shortName", 
														required = false) String id) {
		LOG.info("Requested suggest for short name = " + id);		
		List<String> s = suggest.suggest(id);
		
		if (!s.isEmpty()) {
			return new ResponseEntity<>(s, HttpStatus.CREATED);
		} else {
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/suggestSynonym", method = RequestMethod.GET)
	public ResponseEntity<List<String>> suggestSynonym(@RequestParam(value = "shortName", 
														required = false) String userWord){
		LOG.info("Requested suggest Synonym for short name = " + userWord);		
		List<String> s = suggestSynonym.suggestSynonym(userWord);
		
		if (!s.isEmpty()) {
			return new ResponseEntity<>(s, HttpStatus.CREATED);
		} else {			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@MessageMapping("/suggest")
	@SendTo("/topic/suggest")
	public Object getSuggesWebSocket(SuggestRequestMessage message) {
		LOG.info("Requested suggest for short name = " + message.getId());		
		
		Map<String, List<String>> suggestions =  new HashMap<String, List<String>>();
		List<String> sug = suggest.suggest(message.getId());
		List<String> synonym = suggestSynonym.suggestSynonym(message.getId());
		suggestions.put("suggestion", sug);
		suggestions.put("synonyms", synonym);
		
		if (sug.isEmpty()&&synonym.isEmpty()) {
			return new ErrorMessage("Suggest for word " + message.getId() + " not found");
		}
		return suggestions;
	}
		
}
