package urlshortener.team.web;

import com.google.common.hash.Hashing;
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
import urlshortener.team.message.ErrorMessage;
import urlshortener.team.repository.ShortURLRepository;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionController.class);

    @Autowired
    private ShortURLRepository shortURLRepository;

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<?> shorten(@RequestParam("url") String url,
                                     @RequestParam(value = "sponsor", required = false) String sponsor,
                                     @RequestParam(value = "error", defaultValue = "L") String error,
                                     @RequestParam(value = "vcardname", required = false) String vcardName,
                                     @RequestParam(value = "vcardsurname", required = false) String vcardSurname,
                                     @RequestParam(value = "vcardorganization", required = false) String vcardOrganization,
                                     @RequestParam(value = "vcardtelephone", required = false) String vcardTelephone,
                                     @RequestParam(value = "vcardemail", required = false) String vcardEmail,
                                     HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);
        VCard vcard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, url);
        String hash = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
        ShortURL su = createAndSaveIfValid(hash, url, sponsor, error, vcard, UUID.randomUUID().toString(), request.getRemoteAddr());
        if (su != null) {
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new ErrorMessage("Invalid URI"), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/brandedLink", method = RequestMethod.POST)
    public ResponseEntity<?> shorten(@RequestParam("url") String url,
                                     @RequestParam(value = "shortName", required = false) String shortName,
                                     @RequestParam(value = "sponsor", required = false) String sponsor,
                                     @RequestParam(value="error", defaultValue = "L") String error,
                                     @RequestParam(value = "vcardname", required = false) String vcardName,
                                     @RequestParam(value = "vcardsurname", required = false) String vcardSurname,
                                     @RequestParam(value = "vcardorganization", required = false) String vcardOrganization,
                                     @RequestParam(value = "vcardtelephone", required = false) String vcardTelephone,
                                     @RequestParam(value = "vcardemail", required = false) String vcardEmail,
                                     HttpServletRequest request) {
        logger.info("Requested new short for uri " + url + " and short name " + shortName);
        VCard vcard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, url);
        if (shortURLRepository.findByKey(shortName) == null) {
            ShortURL su = createAndSaveIfValid(shortName, url, sponsor, error, vcard, UUID.randomUUID().toString(), request.getRemoteAddr());
            if (su != null) {
                HttpHeaders h = new HttpHeaders();
                h.setLocation(su.getUri());
                return new ResponseEntity<>(su, h, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new ErrorMessage("Invalid URI"), HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.info("Requested short name already exists");
            return new ResponseEntity<>(new ErrorMessage("Requested shortname already exists"), HttpStatus.BAD_REQUEST);
        }
    }

    private ShortURL createAndSaveIfValid(String id, String url, String sponsor, String error, VCard vcard, String owner, String ip) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        if (urlValidator.isValid(url)) {
            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ShortURL su = new ShortURL(id, url, linkTo(methodOn(RedirectionController.class).redirectTo(id, null)).toUri(),
                    sponsor, new Date(), owner, true, ip, null, user instanceof User ? ((User) user).getUsername() : null);
            try {
                String qrUri = su.getUri().toString() + "/qrcode?error=" + error;
                qrUri += (vcard.getName() != null ? vcard.getUrlEncodedParameters() : "");
                su.setQrLink(new URI(qrUri));
            } catch (URISyntaxException e) {
                logger.error(e.getMessage(), e);
            }
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

}
