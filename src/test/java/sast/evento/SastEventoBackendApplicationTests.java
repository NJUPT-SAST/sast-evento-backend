package sast.evento;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.stereotype.Component;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.DepartmentMapper;
import sast.evento.mapper.SubscribeDepartmentMapper;
import sast.evento.model.Action;
import sast.evento.model.treeDataNodeDTO.AntDesignTreeDataNode;
import sast.evento.model.treeDataNodeDTO.SemiTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.service.CodeService;
import sast.evento.service.LocationService;
import sast.evento.service.LoginService;

import sast.evento.utils.*;
import sast.sastlink.sdk.model.UserInfo;
import sast.sastlink.sdk.model.response.AccessTokenResponse;
import sast.sastlink.sdk.model.response.RefreshResponse;
import sast.sastlink.sdk.service.SastLinkService;
import sast.sastlink.sdk.service.impl.RestTemplateSastLinkService;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sast.sastlink.sdk.enums.GrantType.REFRESH_TOKEN;
import static sast.sastlink.sdk.enums.SastLinkApi.ACCESS_TOKEN;

@SpringBootTest
class SastEventoBackendApplicationTests {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LocationService locationService;
    @Resource
    private RestTemplateSastLinkService sastLinkService;

    @Resource
    private SubscribeDepartmentMapper subscribeDepartmentMapper;

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
    void RedisTest() {
        System.out.println(JsonUtil.toJson(locationService.getLocations()));
    }

    @Test
    void SastLinkTest() {
        sastLinkService.login("","");
    }
    @Test
    void subscribeDepartmentMapperTest(){
        System.out.println(subscribeDepartmentMapper.selectSubscribeDepartmentUser(List.of(1,10,2,3,4)));
    }


}
