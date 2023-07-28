package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sast.evento.config.ActionRegister;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.QrCodeUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        try {
            BufferedImage image = QrCodeUtil.generateQrCode("");
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.QRCODE_ERROR);
        }
    }

    @Test
    void wxSubscribe() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("key1", "");
        dataMap.put("key2", String.valueOf(111));
        dataMap.put("key3", String.valueOf(111));
        System.out.println(JsonUtil.toJson(WxSubscribeRequest.getData(dataMap)));
        System.out.println(JsonUtil.toJson(new AccessTokenRequest()));
    }

}
