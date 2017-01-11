package urlshortener.team.web;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.sun.jndi.toolkit.url.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.team.domain.ShortURL;
import urlshortener.team.repository.ShortURLRepository;
import urlshortener.common.domain.ShortURL;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
public class QRCodeController {

	/**
	 * @param width - anchura del codigo QR (200 es adecuado)
	 * @param height - altura del codigo QR (200 es adecuado)
	 * @throws WriterException
	 * @throws IOException
	 */

	//private final AtomicLong counter = new AtomicLong();

	@Autowired
	protected ShortURLRepository shortURLRepository;

	@RequestMapping("/{hash}/qrcode")
	public void qrCode (@PathVariable String hash,
						@RequestParam(value="width", defaultValue="200") int width,
						@RequestParam(value="height", defaultValue="200") int height,
						@RequestParam(value="vcardname", required = false) String vcardName,
						@RequestParam(value="vcardsurname", required = false) String vcardSurname,
						@RequestParam(value="vcardorganization", required = false) String vcardOrganization,
						@RequestParam(value="vcardtelephone", required = false) String vcardTelephone,
						@RequestParam(value="vcardemail", required = false) String vcardEmail,
						HttpServletResponse response)
			throws WriterException, IOException, URISyntaxException {

		System.out.println("HASH:" + hash);
		ShortURL l = shortURLRepository.findByKey(hash);
		if (l != null) {
			System.out.println("Estoy antes del if de vCard");
			System.out.println("Vcard:" + vcardName + " " + vcardSurname);
			URI uri = linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(hash, null)).toUri();

			if (vcardName!=null) {
				Writer writer = new QRCodeWriter();
				System.out.println("Vcard: URL = "+ l.getTarget() + " Name = " + vcardName + " Surname " + vcardSurname);
				String vcardString = "BEGIN:VCARD\n" +
						"VERSION:2.1\n" +
						"FN:" + vcardName;

				if (vcardSurname != null) {
					vcardString += " " + vcardSurname + "\n";
				}
				if(vcardOrganization != null) {
					vcardString += "ORG:" + vcardOrganization + "\n";
				}
				if(vcardTelephone != null) {
					vcardString += "TEL:" + vcardTelephone + "\n";
				}
				if(vcardEmail != null) {
					vcardString+="EMAIL:" + vcardEmail + "\n";
				}

				vcardString += "URL:" + uri.toString() + "\n";
				vcardString += "END:VCARD";

				System.out.println(vcardString);

				//BitMatrix bitMatrix = writer.encode("localhost:8080/"+hash+"/qrcode", BarcodeFormat.QR_CODE, width, height);
				BitMatrix bitMatrix = writer.encode(vcardString, BarcodeFormat.QR_CODE, width, height);

				OutputStream stream = response.getOutputStream();
				response.setContentType(MediaType.IMAGE_PNG_VALUE);
				MatrixToImageWriter.writeToStream(bitMatrix, "png", stream);

				System.out.println("QR con vCard generado... ");
			}
			else {
				Writer writer = new QRCodeWriter();
				System.out.println("MiControlador "+ l.getTarget());

				//BitMatrix bitMatrix = writer.encode("localhost:8080/"+hash+"/qrcode", BarcodeFormat.QR_CODE, width, height);
				BitMatrix bitMatrix = writer.encode(uri.toString(), BarcodeFormat.QR_CODE, width, height);

				OutputStream stream = response.getOutputStream();
				response.setContentType(MediaType.IMAGE_PNG_VALUE);
				MatrixToImageWriter.writeToStream(bitMatrix, "png", stream);

				System.out.println("QR generado... ");
			}

		}
		else {
			System.out.println ("QR no encontrado... ");
		}

	}
}

