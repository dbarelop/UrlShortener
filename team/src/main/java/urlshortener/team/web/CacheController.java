package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.team.domain.CachedPage;
import urlshortener.team.exception.NotFoundException;
import urlshortener.team.service.CacheService;

@Controller
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/cache/{hash}", method = RequestMethod.GET, produces = "text/http")
    public String getCachedPage(@PathVariable String hash, Model model) {
        CachedPage cachedPage = cacheService.getCachedPage(hash);
        if (cachedPage != null) {
            logger.info("** Serving cached version of " + hash + " from " + cachedPage.getDate());
            model.addAttribute("cachedPage", cachedPage);
            return "cache";
        } else {
            throw new NotFoundException("No cached version available for the requested URL");
        }
    }
}
