package sast.evento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.SemiTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.service.CodeService;
import sast.evento.service.QrCodeCheckInService;
import sast.evento.utils.JsonUtil;
import sast.evento.utils.JwtUtil;
import sast.evento.utils.QRCodeUtil;
import sast.evento.utils.SpringContextUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SastEventoBackendApplicationTests {

    @Test
    void genereateToken() {
        JwtUtil jwtUtil = SpringContextUtil.getBean(JwtUtil.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", "1");
        String token = jwtUtil.generateToken(map);
        System.out.println(token);
    }

    @Test
    void getAllMethodNameByJson() {
        List<String> adminMethods = ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.ADMIN))
                .map(Action::getMethodName).toList();
        List<String> managerMethods = ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.MANAGER))
                .map(Action::getMethodName).toList();
        String adminJson = JsonUtil.toJson(adminMethods);
        System.out.println("admin json: " + adminJson);
        System.out.println("admin max length: " + adminJson.length());
        String managerJson = JsonUtil.toJson(managerMethods);
        System.out.println("manager json: " + managerJson);
        System.out.println("manager max length: " + managerJson.length());
    }

    @Test
    void generateQrCode() {
        try {
            BufferedImage image = QRCodeUtil.generateQrCode("");
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
        WxSubscribeRequest wxSubscribeRequest = new WxSubscribeRequest();
        wxSubscribeRequest.setData(WxSubscribeRequest.getData(dataMap));
        System.out.println(JsonUtil.toJson(wxSubscribeRequest));
        System.out.println(JsonUtil.toJson(new AccessTokenRequest()));
    }

    @Test
    void refreshJobTest() {
        Integer eventId = 1000000;
        QrCodeCheckInService service = SpringContextUtil.getBean(QrCodeCheckInService.class);
        service.getCheckInQrCode(eventId);
        CodeService codeService = SpringContextUtil.getBean(CodeService.class);
        String code = codeService.getCode(eventId);
        System.out.println("code: " + code);
        System.out.println("check: " + service.checkCode(eventId, code));
        codeService.refreshCode(eventId);
        System.out.println("check: " + service.checkCode(eventId, code));
        service.close(eventId);
    }

    @Test
    void CosUtilTest() {
    }

    @Test
    void TreeJsonTest() {
    }


}
