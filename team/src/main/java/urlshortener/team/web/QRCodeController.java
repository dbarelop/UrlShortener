package urlshortener.team.web;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
//import java.util.concurrent.atomic.AtomicLong;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.team.domain.QRCode;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;


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
	public void qrCode (@PathVariable String hash, @RequestParam(value="width", defaultValue="200") int width,
						@RequestParam(value="height", defaultValue="200") int height,
						HttpServletResponse response)
			throws WriterException, IOException, URISyntaxException {

		System.out.println("HASH:" + hash);
		ShortURL l = shortURLRepository.findByKey(hash);
		if (l != null) {

			Writer writer = new QRCodeWriter();
			System.out.println("MiControlador "+ l.getTarget());

			//BitMatrix bitMatrix = writer.encode("localhost:8080/"+hash+"/qrcode", BarcodeFormat.QR_CODE, width, height);
			BitMatrix bitMatrix = writer.encode(l.getTarget().toString(), BarcodeFormat.QR_CODE, width, height);

			OutputStream stream = response.getOutputStream();
			response.setContentType(MediaType.IMAGE_PNG_VALUE);
			MatrixToImageWriter.writeToStream(bitMatrix, "png", stream);

			System.out.println("QR generado... ");
		}
		else {
			System.out.println ("QR no encontrado... ");
		}

	}
}

