package sast.evento.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/26 17:47
 */
public class QRCodeUtil {

    /* 默认大小二维码 */
    public static BufferedImage generateQrCode(String contents) throws Exception {
        return generateQrCode(250, 250, contents, "");
    }

    /* 选择大小二维码 */
    public static BufferedImage generateQrCode(Integer width, Integer height, String contents) throws Exception {
        return generateQrCode(width, height, contents, "");
    }

    /* 选择大小可带logo二维码 */
    public static BufferedImage generateQrCode(Integer width, Integer height, String contents, String logoPath) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
        MatrixToImageConfig config = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
        if (logoPath.isEmpty()) {
            return bufferedImage;
        }
        Graphics2D g2 = bufferedImage.createGraphics();
        BufferedImage logo = ImageIO.read(new File(logoPath));
        g2.drawImage(logo, width / 5 * 2, height / 5 * 2, width / 5, height / 5, null);
        g2.dispose();
        bufferedImage.flush();
        return bufferedImage;
    }

}
