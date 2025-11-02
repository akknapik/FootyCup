package com.tournament.app.footycup.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrCodeService {

    public byte[] generateQrCode(String content, int width, int height) {
        var writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            try (var outputStream = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
                return outputStream.toByteArray();
            }
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Unable to generate QR code", e);
        }
    }
}