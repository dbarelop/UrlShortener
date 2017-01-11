package urlshortener.team.web;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import com.google.zxing.WriterException;

import org.springframework.beans.factory.annotation.Autowired;
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
public class QRCodeController {
	private static Logger logger = Logger.getLogger(QRCodeController.class.getName());

	@Autowired
	private QRCodeService qrService;
	@Autowired
	protected ShortURLRepository shortURLRepository;

	/**
	 * @param width - anchura del codigo QR (200 es adecuado)
	 * @param height - altura del codigo QR (200 es adecuado)
	 * @throws WriterException
	 * @throws IOException
	 */
	@RequestMapping("/{hash}/qrcode")
	public void qrCode (@PathVariable String hash,
						@RequestParam(value="width", defaultValue="200") int width,
						@RequestParam(value="height", defaultValue="200") int height,
						@RequestParam(value="vcardname", required = false) String vcardName,
						@RequestParam(value="vcardsurname", required = false) String vcardSurname,
						@RequestParam(value="vcardorganization", required = false) String vcardOrganization,
						@RequestParam(value="vcardtelephone", required = false) String vcardTelephone,
						@RequestParam(value="vcardemail", required = false) String vcardEmail,
						HttpServletResponse response) throws WriterException, IOException, URISyntaxException {

		ShortURL shortURL = shortURLRepository.findByKey(hash);
		if (shortURL != null) {
			URI uri = linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(hash, null)).toUri();
			OutputStream stream = response.getOutputStream();
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
			try {
				if (vcardName != null) {
					VCard vCard = new VCard(vcardName, vcardSurname, vcardOrganization, vcardTelephone, vcardEmail, uri.toString());
					qrService.generateQRCode(uri, vCard, width, height, stream);
				} else {
					qrService.generateQRCode(uri, width, height, stream);
				}
			} catch (IOException e) {
				logger.warning("Error creating QR code: " + e.getMessage());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}

