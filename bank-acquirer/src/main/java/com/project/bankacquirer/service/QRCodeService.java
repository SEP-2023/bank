package com.project.bankacquirer.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.project.bankacquirer.dto.QRCodeDto;
import com.project.bankacquirer.model.Transaction;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

@Service
public class QRCodeService {

    @Autowired
    TransactionService transactionService;

    public QRCodeDto generateQRCode(String id) {
        String paymentInfo = generatePaymentInfo(id);
        ByteArrayOutputStream stream = QRCode.from(paymentInfo).to(ImageType.PNG).stream();
        byte[] base64bytes = Base64.encodeBase64(stream.toByteArray());
        QRCodeDto dto = new QRCodeDto();
        dto.setQr(new String(base64bytes, StandardCharsets.UTF_8));
        return dto;
    }

    private String generatePaymentInfo(String id) {
        Transaction t = transactionService.findById(Long.valueOf(id));

        return "amount:" + t.getAmount() +
                "|paymentId:" + t.getId() +
                "|merchantName:" + t.getAcquirer().getUser().getName() +
                "|account:" + t.getAcquirer().getNumber();
    }

    public String decodeQRCode(String code) throws IOException, NotFoundException {
        byte[] qrCodeImageBytes = Base64.decodeBase64(code);
        BufferedImage qrCodeImage = ImageIO.read(new ByteArrayInputStream(qrCodeImageBytes));

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(qrCodeImage)));
        Result result = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }

    public boolean validateQRCode(String qr, Transaction t) throws NotFoundException, IOException {
        String text = decodeQRCode(qr);

        String[] keyValuePairs = text.split("\\|");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "amount":
                    if (!value.equals(String.valueOf(t.getAmount()))) {
                        return false;
                    }
                    break;
                case "paymentId":
                    if (!value.equals(String.valueOf(t.getId()))) {
                        return false;
                    }
                    break;
                case "merchantName":
                    if (!value.equals(t.getAcquirer().getUser().getName())) {
                        return false;
                    }
                    break;
                case "account":
                    if (!value.equals(t.getAcquirer().getNumber())) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

}
