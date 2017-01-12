package urlshortener.team.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.google.common.hash.Hashing;

import eu.bitwalker.useragentutils.UserAgent;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.Click;
import urlshortener.team.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.team.domain.VCard;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.service.StatusServiceImpl;

@RestController
public class UrlShortenerControllerWithLogs {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Autowired
	private StatusService statusService;
	@Autowired
	private MetricsController metricsController;
	@Autowired
	private ShortURLRepository shortURLRepository;
	@Autowired
	private ClickRepository clickRepository;

	@RequestMapping(value = "/{id:(?!link|index|metrics|404).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		ShortURL l = shortURLRepository.findByKey(id);
		if (l != null) {
			ResponseEntity<?> response;
			if (l.getLastStatus() == HttpStatus.OK) {
				response = createSuccessfulRedirectToResponse(l);
			} else {
				response = badStatus(l);
			}
			createAndSaveClick(id, extractIP(request), extractUserAgent(request));
			metricsController.notifyNewMetrics(id);
			return response;
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  @RequestParam(value = "error", defaultValue = "L") String error,
											  @RequestParam(value = "vcardname", required = false) String vcardName,
											  @RequestParam(value = "vcardsurname", required = false) String vcardSurname,
											  @RequestParam(value = "vcardorganization", required = false) String vcardOrganization,
											  @RequestParam(value = "vcardtelephone", required = false) String vcardTelephone,
											  @RequestParam(value = "vcardemail", required = false) String vcardEmail,
											  HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		statusService.verifyStatus(url);
		VCard vcard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, url);
		ShortURL su = createAndSaveIfValid(url, sponsor, error, vcard, UUID.randomUUID().toString(), request.getRemoteAddr());
        statusService.verifyStatus(su);
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	private ShortURL createAndSaveIfValid(String url, String sponsor, String error, VCard vcard, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
			Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ShortURL su = new ShortURL(id, url,
					linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(id, null)).toUri(), sponsor, new Date(System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null, user instanceof User ? ((User) user).getUsername() : null);
			try {
				String qrUri = su.getUri().toString() + "/qrcode?error=" + error;
				qrUri += (vcard.getName() != null ? vcard.getUrlEncodedParameters() : "");
				su.setQRLink(new URI(qrUri));
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}

	private void createAndSaveClick(String hash, String ip, UserAgent userAgent) {
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				null, userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), ip, null);
		cl=clickRepository.save(cl);
		logger.info(cl!=null?"["+hash+"] saved with id ["+cl.getId()+"]":"["+hash+"] was not saved");
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	private UserAgent extractUserAgent(HttpServletRequest request) {
		String userAgentString = request.getHeader("User-Agent");
		return UserAgent.parseUserAgentString(userAgentString);
	}

	private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
	}

	private ResponseEntity<?> badStatus(ShortURL shortURL) throws URISyntaxException {
		HttpHeaders h = new HttpHeaders();
		URI location = linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo("/404/" + shortURL.getHash(), null)).toUri();
		h.setLocation(location);
		return new ResponseEntity<>(h, HttpStatus.valueOf(shortURL.getMode()));
	}
}
