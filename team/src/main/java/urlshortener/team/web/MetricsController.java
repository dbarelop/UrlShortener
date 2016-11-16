package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.team.domain.Metrics;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;
    @Autowired
    private ClickRepository clickRepository;

    @RequestMapping(value = "/metrics/{hash}", method = RequestMethod.GET)
    public ResponseEntity<Metrics> getMetrics(@PathVariable String hash) {
        logger.info("Requested metrics for link with hash " + hash);
        ShortURL shortURL = shortURLRepository.findByKey(hash);
        if (shortURL == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Long clicks = clickRepository.clicksByHash(hash);
        URI uri = linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri();
        Metrics metrics = new Metrics(uri, shortURL, clicks);
        return new ResponseEntity<>(metrics, HttpStatus.OK);
    }
}
