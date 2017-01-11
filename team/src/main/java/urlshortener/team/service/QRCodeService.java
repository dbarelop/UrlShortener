package urlshortener.team.service;

import com.google.zxing.WriterException;
import urlshortener.team.domain.VCard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public interface QRCodeService {

    void generateQRCode(URI uri, int width, int height, OutputStream stream) throws WriterException, IOException;
    void generateQRCode(URI uri, VCard vcard, int width, int height, OutputStream stream)  throws WriterException, IOException;
}
