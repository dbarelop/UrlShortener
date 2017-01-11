package urlshortener.team.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import urlshortener.common.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MappingController {
	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private StatusService statusService;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET, produces = "text/http")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET, produces = "text/http")
    public String metrics() {
        return "metrics";
    }
    
    @RequestMapping(value = "/404/{hash}", method = RequestMethod.GET, produces = "text/http")
    public String badStatus(@PathVariable String hash, Model model) {
    	ShortURL shortURL = shortURLRepository.findByKey(hash);
    	model.addAttribute("uri", linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(shortURL.getHash(), null)).toUri());
        model.addAttribute("date", shortURL.getBadStateDate());
        model.addAttribute("target", shortURL.getTarget());
        model.addAttribute("hash", "http://localhost:8080/cache/" + shortURL.getHash());
    	return "404";
    }
    
    @RequestMapping(value = "/cache/{hash}", method = RequestMethod.GET, produces = "text/http")
    public String viewCache(@PathVariable String hash) throws IOException {
    	ShortURL shortURL = shortURLRepository.findByKey(hash);
    	String bodyHTML = statusService.getCacheStaticPage(shortURL);
    	if (bodyHTML != null) {
    		System.out.println(bodyHTML);
	    	return "index";
		} else {
			System.out.println(bodyHTML);
			return "index";
		}
    }
}
