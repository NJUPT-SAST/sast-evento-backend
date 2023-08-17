package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Participate;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.Action;
import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;
import sast.evento.service.EventService;
import sast.evento.service.ParticipateService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private EventService eventService;
    @Resource
    private ParticipateService participateService;

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
    @OperateLog("订阅活动 / 取消订阅")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribe")
    public String subscribe(@RequestParam Integer eventId,
                            @RequestParam Boolean isSubscribe) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return participateService.subscribe(userIdInt, eventId, isSubscribe);
    }

    /**
     */
    @OperateLog("获取已订阅的活动列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribed")
    public List<EventModel> getSubscribed() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return eventService.getSubscribed(userIdInt);
    }

    /**
     */
    @OperateLog("报名活动")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/register")
    public String register(@RequestParam Integer eventId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return participateService.register(userIdInt, eventId);
    }

    /**
     */
    @OperateLog("获取已报名的活动列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/registered")
    public List<EventModel> getRegistered() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return eventService.getRegistered(userIdInt);
    }

    /**
     */
    // 查询用户自己是否报名、订阅、参加（即签到）活动
    // 若无结果，则表示用户没有报名、没有订阅、更没有签到。
    @OperateLog("获取个人的活动的状态")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/participate")
    public Participate getParticipation(@RequestParam Integer eventId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return participateService.getParticipation(userIdInt, eventId);
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