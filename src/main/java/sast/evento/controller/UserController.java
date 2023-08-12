package sast.evento.controller;

import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.model.UserProFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     */
    @OperateLog("获取个人信息")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/info")
    public UserProFile getUser(@RequestParam String userId) {
        /* 等着和sastLink对接捏 */
        return null;
    }

    /**
     */
    @OperateLog("更改个人信息")
    @DefaultActionState(ActionState.LOGIN)
    @PutMapping("/info")
    public String putUser(@RequestParam String userId,
                          @RequestBody UserProFile userProFile) {
        if (!userProFile.getUserId().equals(userId))
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        /* 等着和sastLink对接捏 */
        return null;
    }

    /**
     */
    @OperateLog("报名订阅活动")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribe")
    public String subscribe(@RequestParam Integer eventId,
                            @RequestParam Boolean isSubscribe) {
        return null;
    }

    @OperateLog("获取查看个人全部可用接口")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/action")
    public List<Action> getAllPermissions() {
        return null;
    }

    @OperateLog("获取查看个人admin权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/admin")
    public List<Action> getAdminPermissions() {
        return null;
    }

    @OperateLog("获取查看个人某event权限")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/manager")
    public List<Action> getManagePermissions(@RequestParam Integer eventId) {
        return null;
    }

    @OperateLog("获取个人可管理的活动")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/manager/events")
    public List<Action> getManageEvents() {
        return null;
    }


}