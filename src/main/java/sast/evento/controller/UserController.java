package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.UserProFile;

@Controller
@RequestMapping("/user")
public class UserController {

    @OperateLog("获取个人信息")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/info")
    public UserProFile getUser(@RequestParam String userId) {
        /* 等着和sastLink对接捏 */
        return null;
    }

    @OperateLog("更改个人信息")
    @DefaultActionState(ActionState.PUBLIC)
    @PutMapping("/info")
    public String putUser(@RequestParam String userId,
                          @RequestBody UserProFile userProFile) {
        if (!userProFile.getUserId().equals(userId))
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        /* 等着和sastLink对接捏 */
        return null;
    }

    @OperateLog("报名订阅活动")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/subscribe")
    public String userSubscribeGet(@RequestParam Integer eventId,
                                   @RequestParam Boolean isSubscribe) {
        return null;
    }

}