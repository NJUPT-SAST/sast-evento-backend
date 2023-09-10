package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;

import java.awt.image.BufferedImage;


@Controller
@RequestMapping("/event")
public class CheckInController {

    /* 由后端生成部分信息置于二维码，userId需要前端填充 */
    @OperateLog("签到")
    @DefaultActionState(ActionState.LOGIN)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/checkIn")
    @ResponseBody
    public String CheckIn(@RequestParam @EventId Integer eventId,
                          @RequestParam String userId,
                          @RequestParam String code) {
        throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"not finish");
    }

    @OperateLog("获取活动签到二维码")
    @DefaultActionState(value = ActionState.ADMIN,group = "checkIn")/* 这里为admin,eventId注解没什么用 */
    @GetMapping("/qrcode")
    @ResponseBody
    public BufferedImage getEventQrcode(@RequestParam @EventId Integer eventId) {
        throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"not finish");
    }


}
