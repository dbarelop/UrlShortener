package urlshortener.team.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MappingController {
	@Autowired
	private ShortURLRepository shortURLRepository;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String metrics() {
        return "metrics";
    }
    
    @RequestMapping(value = "/404/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String badStatus(@PathVariable String hash, Model model) {
    	ShortURL shortURL = shortURLRepository.findByKey(hash);
    	model.addAttribute("uri", linkTo(methodOn(RedirectionController.class).redirectTo(shortURL.getHash(), null)).toUri());
        model.addAttribute("date", shortURL.getLastCheck());
        model.addAttribute("target", shortURL.getTarget());
        // TODO: remove hardcoded port and host
        model.addAttribute("hash", "http://localhost:8080/cache/" + shortURL.getHash());
    	return "404";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String login() {
        return "login";
    }
}
