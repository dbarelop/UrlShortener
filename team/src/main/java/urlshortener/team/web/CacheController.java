package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.team.domain.CachedPage;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.exception.NotFoundException;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.service.CacheService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/cache/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> getCachedPage(@PathVariable String hash) {
        CachedPage cachedPage = cacheService.getCachedPage(hash);
        if (cachedPage != null) {
            logger.info("** Serving cached version of " + hash + " from " + cachedPage.getDate());
            return new ResponseEntity<>(cachedPage.getBody(), HttpStatus.OK);
        } else {
            throw new NotFoundException("No cached version available for the requested URL");
        }
    }

    @RequestMapping(value = "/404/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String badStatus(@PathVariable String hash, Model model) {
        ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL != null && (shortURL.getLastStatus() != HttpStatus.OK || !shortURL.isValid())) {
            if (shortURL.getCacheDate() != null) {
                model.addAttribute("cacheUri", linkTo(methodOn(RedirectionController.class).redirectTo("cache/" + shortURL.getHash(), null)).toUri());
                model.addAttribute("cacheDate", shortURL.getCacheDate());
            } else {
                model.addAttribute("cacheUri", null);
            }
            model.addAttribute("lastCheckDate", shortURL.getLastCheckDate());
            return "cache";
        } else if (shortURL != null) {
            // If the target URL is OK redirect to target
            return "redirect:" + linkTo(methodOn(RedirectionController.class).redirectTo(shortURL.getHash(), null));
        } else {
            throw new NotFoundException("The requested page was not found");
        }
    }
}
