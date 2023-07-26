package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sast.evento.config.ActionRegister;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.QrCodeUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@SpringBootTest
class SastEventoBackendApplicationTests {

    @Test
    void getAllMethodNameByJson() {
        String json = JsonUtil.toJson(new ArrayList<>(ActionRegister.actionNameSet));
        System.out.println("json: " + json);
        System.out.println("max lengh: " + json.length());
    }

    @Test
    void generateQrCode() {
        BufferedImage image = QrCodeUtil.generateQrCode("");
    }

}
