package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.team.domain.CachedPage;
import urlshortener.team.exception.NotFoundException;
import urlshortener.team.service.CacheService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/cache/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> getCachedPage(@PathVariable String hash, HttpServletResponse response) {
        CachedPage cachedPage = cacheService.getCachedPage(hash);
        if (cachedPage != null) {
            logger.info("** Serving cached version of " + hash + " from " + cachedPage.getDate());
            return new ResponseEntity<>(cachedPage.getBody(), HttpStatus.OK);
        } else {
            throw new NotFoundException("No cached version available for the requested URL");
        }
    }
}
