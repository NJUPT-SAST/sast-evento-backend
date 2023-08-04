package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.service.SastLinkServiceCacheAble;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:31
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private SastLinkServiceCacheAble sastLinkServiceCacheAble;

    @OperateLog("link登录")
    @PostMapping("/link")
    @DefaultActionState(ActionState.PUBLIC)
    public String linkLogin(@RequestParam String code) {
        return sastLinkServiceCacheAble.linkLogin(code);//todo 对接sast link
    }

    @OperateLog("wx登录")
    @PostMapping("/wx")
    @DefaultActionState(ActionState.PUBLIC)
    public String wxLogin(@RequestParam String code) {
        return sastLinkServiceCacheAble.wxLogin(code);//todo 对接sast link
    }



}
