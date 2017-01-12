package urlshortener.team.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;
import urlshortener.team.domain.VCard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public void generateQRCode(URI uri, int width, int height, OutputStream stream, ErrorCorrectionLevel errorCorrectionLevel) throws WriterException, IOException {
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        if (errorCorrectionLevel != null) {
            Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
            bitMatrix = writer.encode(uri.toString(), BarcodeFormat.QR_CODE, width, height, hints);
        } else {
            bitMatrix = writer.encode(uri.toString(), BarcodeFormat.QR_CODE, width, height);
        }
        MatrixToImageWriter.writeToStream(bitMatrix, "png", stream);
    }

    @Override
    public void generateQRCode(URI uri, VCard vcard, int width, int height, OutputStream stream, ErrorCorrectionLevel errorCorrectionLevel)  throws WriterException, IOException {
        Writer writer = new QRCodeWriter();
        String vcardString = "BEGIN:VCARD\n" +
                             "VERSION:2.1\n";
        vcardString += "FN:" + vcard.getName() + (vcard.getSurname() != null ? vcard.getSurname() : "") + "\n";
        vcardString += (vcard.getOrganization() != null ? "ORG:" + vcard.getOrganization() + "\n" : "");
        vcardString += (vcard.getTelephone() != null ? "TEL:" + vcard.getTelephone() + "\n" : "");
        vcardString += (vcard.getEmail() != null ? "EMAIL:" + vcard.getEmail() + "\n" : "");
        vcardString += "URL:" + uri.toString() + "\n";
        vcardString += "END:VCARD";

        BitMatrix bitMatrix;
        if (errorCorrectionLevel != null) {
            Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
            bitMatrix = writer.encode(vcardString, BarcodeFormat.QR_CODE, width, height, hints);
        } else {
            bitMatrix = writer.encode(vcardString, BarcodeFormat.QR_CODE, width, height);
        }
        MatrixToImageWriter.writeToStream(bitMatrix, "png", stream);
    }
}
