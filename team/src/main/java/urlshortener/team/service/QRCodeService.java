package urlshortener.team.service;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import urlshortener.team.domain.VCard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public interface QRCodeService {

    void generateQRCode(URI uri, int width, int height, OutputStream stream, ErrorCorrectionLevel errorCorrectionLevel) throws WriterException, IOException;
    void generateQRCode(URI uri, VCard vcard, int width, int height, OutputStream stream, ErrorCorrectionLevel errorCorrectionLevel)  throws WriterException, IOException;
}
