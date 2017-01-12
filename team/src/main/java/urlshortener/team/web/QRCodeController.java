package urlshortener.team.web;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.zxing.WriterException;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.VCard;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.service.QRCodeService;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QRCodeController {
	private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);

	@Autowired
	private QRCodeService qrService;
	@Autowired
	protected ShortURLRepository shortURLRepository;

	@RequestMapping("/{hash}/qrcode")
	public void qrCode (@PathVariable String hash,
						@RequestParam(value="width", defaultValue="200") int width,
						@RequestParam(value="height", defaultValue="200") int height,
						@RequestParam(value="error", required = false) String error,
						@RequestParam(value="vcardname", required = false) String vcardName,
						@RequestParam(value="vcardsurname", required = false) String vcardSurname,
						@RequestParam(value="vcardorganization", required = false) String vcardOrganization,
						@RequestParam(value="vcardtelephone", required = false) String vcardTelephone,
						@RequestParam(value="vcardemail", required = false) String vcardEmail,
						HttpServletResponse response) throws WriterException, IOException, URISyntaxException {

		ShortURL shortURL = shortURLRepository.findByKey(hash);
		if (shortURL != null) {
			URI uri = linkTo(methodOn(RedirectionController.class).redirectTo(hash, null)).toUri();
			OutputStream stream = response.getOutputStream();
			try {
				ErrorCorrectionLevel errorCorrectionLevel = null;
				if (error != null) {
					switch (error) {
						case "L": errorCorrectionLevel = ErrorCorrectionLevel.L; break;
						case "M": errorCorrectionLevel = ErrorCorrectionLevel.M; break;
						case "Q": errorCorrectionLevel = ErrorCorrectionLevel.Q; break;
						case "H": errorCorrectionLevel = ErrorCorrectionLevel.H; break;
					}
				}
				if (vcardName != null) {
					VCard vcard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, uri.toString());
					qrService.generateQRCode(uri, vcard, width, height, stream, errorCorrectionLevel);
				} else {
					qrService.generateQRCode(uri, width, height, stream, errorCorrectionLevel);
				}
				response.setContentType(MediaType.IMAGE_PNG_VALUE);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}

