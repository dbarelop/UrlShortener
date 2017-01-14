package urlshortener.team.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import eu.bitwalker.useragentutils.UserAgent;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.Click;
import urlshortener.team.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.team.repository.ShortURLRepository;

@RestController
@Order(Ordered.LOWEST_PRECEDENCE)
public class RedirectionController {

	private static final Logger logger = LoggerFactory.getLogger(RedirectionController.class);

	@Autowired
	private MetricsController metricsController;
	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private ClickRepository clickRepository;

	@RequestMapping(value = "/{hash}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String hash, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + hash);
		ShortURL l = shortURLRepository.findByKey(hash);
		if (l != null) {
			ResponseEntity<?> response;
			if (l.getLastStatus() == null || l.getLastStatus() != HttpStatus.OK) {
				logger.info("** " + l.getTarget() + " was down during last test");
				response = badStatus(l);
			} else {
				response = createSuccessfulRedirectToResponse(l);
			}
			createAndSaveClick(hash, request.getRemoteAddr(), extractUserAgent(request));
			metricsController.notifyNewMetrics(hash);
			return response;
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	private void createAndSaveClick(String hash, String ip, UserAgent userAgent) {
		Click cl = new Click(null, hash, new Date(), null, userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), ip, null);
		cl = clickRepository.save(cl);
		logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
	}

	private UserAgent extractUserAgent(HttpServletRequest request) {
		String userAgentString = request.getHeader("User-Agent");
		return UserAgent.parseUserAgentString(userAgentString);
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.TEMPORARY_REDIRECT);
	}

	private ResponseEntity<?> badStatus(ShortURL shortURL) {
		HttpHeaders h = new HttpHeaders();
		URI location = linkTo(methodOn(RedirectionController.class).redirectTo("404/" + shortURL.getHash(), null)).toUri();
		h.setLocation(location);
		return new ResponseEntity<>(h, HttpStatus.TEMPORARY_REDIRECT);
	}
}
