package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.sastlink.sdk.service.SastLinkService;

@RestController
@RequestMapping("/test")
public class TestController {
    @Value("${test.challenge}")
    private String challenge;
    @Value("${test.method}")
    private String method;

    @Resource
    private SastLinkService sastLinkService;

    @OperateLog("获取个人信息")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/linklogin")
    public String linkLogin(@RequestParam String email,@RequestParam String password){
        try {
            String token = sastLinkService.login(email,password);
            return sastLinkService.authorize(token,challenge,method);
        }catch (Exception e){
            throw new LocalRunTimeException(ErrorEnum.SAST_LINK_SERVICE_ERROR,e.getMessage());
        }

    }


}
