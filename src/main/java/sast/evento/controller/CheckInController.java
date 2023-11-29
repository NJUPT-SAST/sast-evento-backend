package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.UserModel;
import sast.evento.service.CodeService;
import sast.evento.service.ParticipateService;
import sast.evento.utils.RedisUtil;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/event")
public class CheckInController {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ParticipateService participateService;
    @Resource
    private CodeService codeService;
    /* 由后端生成部分信息置于二维码，userId需要前端填充 */
    @OperateLog("签到")
    @DefaultActionState(ActionState.LOGIN)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/checkIn")
    @ResponseBody
    //和前端讨论后去掉了eventId参数
    public String CheckIn(@RequestParam String code) {
        UserModel user = HttpInterceptor.userHolder.get();
        if (code.isEmpty() ){
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR,"code shouldn't be empty");
        }
        Integer eventId = codeService.getEventIdFromAuthCode(code);
        participateService.participate(user.getId(),eventId,true);
        return "签到成功";
    }

    @OperateLog("生成活动签到二维码的验证码")
    @DefaultActionState(ActionState.ADMIN)/* 这里为admin,eventId注解没什么用 */
    @GetMapping("/authcode")
    @ResponseBody
    public String eventAuthcodeGenerate(@RequestParam @EventId Integer eventId) {
        //generate a random number between 9999 and 99999
        int authcode = (int) ((Math.random() * 9 + 1) * 10000);
        //save the authcode to Redis,expire in 3 minutes
        redisUtil.set("AUTHCODE:" + authcode,eventId, 60 * 3, TimeUnit.SECONDS);
        return Integer.toString(authcode);
    }

}
