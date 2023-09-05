package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.Participate;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;
import sast.evento.service.DepartmentService;
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
    @Resource
    private DepartmentService departmentService;

    @OperateLog("获取个人信息")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/info")
    public UserProFile getUser(@RequestParam String userId) {
        /* 等着和sastLink对接捏 */
        return null;
    }

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

    @OperateLog("订阅活动 / 取消订阅")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribe")
    public String subscribe(@RequestParam Integer eventId,
                            @RequestParam Boolean isSubscribe) {
        User user = HttpInterceptor.userHolder.get();
        return participateService.subscribe(user.getUserId(), eventId, isSubscribe);
    }

    @OperateLog("获取已订阅的活动列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribed")
    public List<EventModel> getSubscribed() {
        User user = HttpInterceptor.userHolder.get();
        return eventService.getSubscribed(user.getUserId());
    }

    @OperateLog("报名活动")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/register")
    public String register(@RequestParam Integer eventId,
                           @RequestParam Boolean isRegister) {
        User user = HttpInterceptor.userHolder.get();
        return participateService.register(user.getUserId(), eventId, isRegister);
    }

    @OperateLog("获取已报名的活动列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/registered")
    public List<EventModel> getRegistered() {
        User user = HttpInterceptor.userHolder.get();
        return eventService.getRegistered(user.getUserId());
    }

    // 查询用户自己是否报名、订阅、参加（即签到）活动
    // 若无结果，则表示用户没有报名、没有订阅、更没有签到。
    @OperateLog("获取个人的活动的状态")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/participate")
    public Participate getParticipation(@RequestParam Integer eventId) {
        User user = HttpInterceptor.userHolder.get();
        return participateService.getParticipation(user.getUserId(), eventId);
    }

    @OperateLog("订阅组别 / 取消订阅")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribe/department")
    public String subscribeDepartment(@RequestParam Integer departmentId,
                                      @RequestParam Boolean isSubscribe) {
        User user = HttpInterceptor.userHolder.get();
        departmentService.subscribeDepartment(user.getUserId(), departmentId, isSubscribe);
        return "ok";
    }

    @OperateLog("获取个人订阅的组别")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/subscribe/departments")
    public List<Department> getSubscribeDepartment() {
        User user = HttpInterceptor.userHolder.get();
        return departmentService.getSubscribeDepartment(user.getUserId());
    }

}