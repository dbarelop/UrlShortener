package urlshortener.team.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import urlshortener.team.message.ErrorMessage;
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
	public Object getSuggesWebSocket(@DestinationVariable String id) {
		LOG.info("Requested suggest for short name = " + id);		
		List<String> s = suggest.suggest(id);

		if (!s.isEmpty()) {
			return new ErrorMessage("Suggest for word " + id + " not found");
		}

		return s;
	}
		
}
