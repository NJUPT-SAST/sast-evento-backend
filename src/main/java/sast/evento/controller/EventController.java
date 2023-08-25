package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Event;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;
import sast.evento.service.EventDepartmentService;
import sast.evento.service.EventService;
import sast.evento.service.PermissionService;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Resource
    private EventService eventService;

    @Resource
    private EventDepartmentService eventDepartmentService;
    @Resource
    private PermissionService permissionService;

    /* 由后端生成部分信息置于二维码，userId需要前端填充 */
    @OperateLog("签到")
    @DefaultActionState(ActionState.LOGIN)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/checkIn")
    public String CheckIn(@RequestParam @EventId Integer eventId,
                          @RequestParam String userId,
                          @RequestParam String code) {
        return null;
    }

    @OperateLog("获取活动签到二维码")
    @DefaultActionState(ActionState.ADMIN)/* 这里为admin,eventId注解没什么用 */
    @GetMapping("/qrcode")
    public BufferedImage eventQrcodeGet(@RequestParam @EventId Integer eventId) {
        return null;
    }

    /**
     *
     */
    @OperateLog("查看所有正在进行的活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/conducting")
    public List<EventModel> getConducting() {
        return eventService.getConducting();
    }

    /**
     *
     */
    @OperateLog("查看最新活动列表（按开始时间正序排列未开始的活动）")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/newest")
    public List<EventModel> getNewest() {
        return eventService.getNewest();
    }

    /**
     *
     */
    @OperateLog("查看用户历史活动列表（参加过已结束）")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/history")
    public List<EventModel> getHistory() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return eventService.getHistory(userIdInt);
    }

    @OperateLog("删除活动")
    @DefaultActionState(ActionState.MANAGER)
    @DeleteMapping("/info")
    public String deleteEvent(@RequestParam @EventId Integer eventId) {
//      // 可以将两步操作放在同一Service层进行，并使用 Transactional 注解
        if (!eventDepartmentService.deleteEventDepartmentsByEventId(eventId)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete eventDepartment failed");
        }
        if (!eventService.deleteEvent(eventId)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete event failed");
        }
        return "success";
    }

    /**
     *
     */
    @OperateLog("获取活动详情")
    @DefaultActionState(ActionState.PUBLIC)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/info")
    public EventModel getEvent(@RequestParam @EventId Integer eventId) {
        return eventService.getEvent(eventId);
    }

    @OperateLog("取消活动")
    @DefaultActionState(ActionState.MANAGER)
    @PatchMapping("/info")
    public String patchEvent(@RequestParam @EventId Integer eventId,
                             @RequestBody Event event) {
        if (!event.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return eventService.cancelEvent(eventId).toString();
    }

    @OperateLog("发起活动（添加活动）")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/info")
    public String addEvent(@RequestBody EventModel eventModel) {
        if (eventModel.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        /* 记得给自己加活动权限鸭喵 */
        /* 检测内容不为null的部分添加 */
        Event event = new Event(eventModel);
        Integer eventId = eventService.addEvent(event);
        if (!eventDepartmentService.addEventDepartments(eventId, eventModel.getDepartments())) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "add eventDepartment failed");
        }
        String[] methods = {"addEvent", "putEvent", "patchEvent", "deleteEvent"};
        List<String> methodNames = new ArrayList<>(Arrays.asList(methods));
        permissionService.addManager(eventId, methodNames, userProFile.getUserId(), null);
        return "success";
    }

    @OperateLog("修改活动")
    @DefaultActionState(ActionState.MANAGER)
    @PutMapping("/info")
    public String putEvent(@RequestParam @EventId Integer eventId,
                           @RequestBody Event event) {
        if (!event.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return eventService.updateEvent(event).toString();
    }

    /**
     *
     */
    @OperateLog("获取活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/list")
    public List<EventModel> getEvents(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEvents(page, size);
    }

    @OperateLog("获取活动列表(筛选)")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/list")
    public List<EventModel> postForEvents(@RequestParam(required = false) List<Integer> typeId,
                                          @RequestParam(required = false) List<Integer> departmentId,
                                          @RequestParam(required = false) String time) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        // 获取未处理location的初步活动列表
        List<EventModel> eventModels = eventService.postForEvents(typeId, departmentId, time);
        // 获取所有处理location后的活动列表
        return eventService.exchangeLocationOfEvents(eventModels);
        // 处理location，从string类型的location_id转换成地址

    }

}
