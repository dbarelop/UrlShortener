package urlshortener.team.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import urlshortener.team.domain.ShortURL;
import urlshortener.team.domain.VCard;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.domain.ShortName;
import urlshortener.team.service.StatusService;
import urlshortener.team.service.StatusServiceImpl;

@RestController
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShortNameController {

	private static final Logger logger = LoggerFactory.getLogger(ShortNameController.class);

	@Autowired
	private StatusService statusService;
	@Autowired
	protected ShortURLRepository shortURLRepository;

	private List<String> words;
	private ShortName shortname;

	public ShortNameController(){
		shortname  = new ShortName();
		words = shortname.getDictionary();
	}

	private String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	@RequestMapping(value = "/brandedLink", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortenerid(@RequestParam("url") String url,
												@RequestParam(value = "shortName", required = false) String id,
												@RequestParam(value = "sponsor", required = false) String sponsor,
												@RequestParam(value="error", defaultValue = "L") String error,
												@RequestParam(value = "vcardname", required = false) String vcardName,
												@RequestParam(value = "vcardsurname", required = false) String vcardSurname,
												@RequestParam(value = "vcardorganization", required = false) String vcardOrganization,
												@RequestParam(value = "vcardtelephone", required = false) String vcardTelephone,
												@RequestParam(value = "vcardemail", required = false) String vcardEmail,
												HttpServletRequest request) {
		logger.info("Requested new short for uri " + url + " and short name = " + id);
		VCard vcard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, url);
		ShortURL su = createAndSaveIfValid(id,url, sponsor, error, vcard, UUID.randomUUID().toString(), extractIP(request));
        statusService.verifyStatus(su);
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private ShortURL createAndSaveIfValid(String id, String url, String sponsor, String error, VCard vcard, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

		if (urlValidator.isValid(url)) {

			String finalId;
			ShortURL l = shortURLRepository.findByKey(id);
			List<ShortURL> ListUrl = shortURLRepository.findByTarget(url);

			if (!(ListUrl.isEmpty())) {
				shortURLRepository.delete(ListUrl.get(0).getHash());
			}

			if (l == null && !id.equals("")) {

				finalId = id;
				suggest(id);

				Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				ShortURL su = new ShortURL(finalId, url,
						linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(finalId, null)).toUri(), sponsor,
						new Date(System.currentTimeMillis()), owner, HttpStatus.TEMPORARY_REDIRECT.value(), true, ip,
						null, user instanceof User ? ((User) user).getUsername() : null);
				try {
					String qrUri = su.getUri().toString() + "/qrcode?error=" + error;
					qrUri += (vcard.getName() != null ? "?" + vcard.getUrlEncodedParameters() : "");
					su.setQRLink(new URI(qrUri));
				} catch (URISyntaxException e) {
					logger.error(e.getMessage(), e);
				}
				return shortURLRepository.save(su);
			} else {
				logger.info("shortName already exist = " + id);
				return null;
			}
		} else {
			logger.info("Link invalid");
			return null;
		}
	}

	private boolean suggest(String UserWord) {

		if (UserWord.isEmpty()) {
			return false;
		}
		//System.out.println("User word: " + UserWord);
		boolean suggestionAdded = false;

		for (String word : words) {
			boolean coincidences = true;
			for (int i = 0; i < UserWord.length(); i++) {

				if (UserWord.length() <= word.length()) {
					if (!UserWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {
						coincidences = false;
						break;
					}
				}
			}
			if (coincidences) {
				if (UserWord.length() < word.length()) {
					addSuggestions(word);
					suggestionAdded = true;
				}
			}
		}
		return suggestionAdded;
	}

	private void addSuggestions(String word) {
		//System.out.println("suggest: " + word);
		//TODO implementar la respuesta de las sugerencias en el cliente
	}
}