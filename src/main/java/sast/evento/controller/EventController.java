package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.Event;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.EventModel;
import sast.evento.model.PageModel;
import sast.evento.model.UserModel;
import sast.evento.service.DepartmentService;
import sast.evento.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Resource
    private EventService eventService;

    @Resource
    private DepartmentService departmentService;

    @OperateLog("查看正在进行活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/conducting")
    public List<EventModel> getConducting() {
        return eventService.getConducting();
    }


    @OperateLog("查看最新活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/newest")
    public List<EventModel> getNewest() {
        return eventService.getNewest();
    }

    @OperateLog("查看用户历史活动列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/history")
    public List<EventModel> getHistory() {
        UserModel user = HttpInterceptor.userHolder.get();
        return eventService.getHistory(user.getId());
    }

    /**
     * 删除活动
     *
     * @param eventId 活动id
     * @return 是否成功
     */
    @OperateLog("删除活动")
    @DefaultActionState(value = ActionState.MANAGER, group = "event")
    @DeleteMapping("/info")
    public String deleteEvent(@RequestParam @EventId Integer eventId) {
        return eventService.deleteEvent(eventId).toString();
    }


    @OperateLog("获取活动详情")
    @DefaultActionState(ActionState.PUBLIC)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/info")
    public EventModel getEvent(@RequestParam @EventId Integer eventId) {
        return eventService.getEvent(eventId);
    }

    /**
     * 取消活动
     *
     * @param eventId 活动id
     * @param event   活动信息
     * @return 是否成功
     */
    @OperateLog("取消活动")
    @DefaultActionState(value = ActionState.MANAGER, group = "event")
    @PatchMapping("/info")
    public String cancelEvent(@RequestParam @EventId Integer eventId,
                              @RequestBody Event event) {
        if (!event.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return eventService.cancelEvent(eventId).toString();
    }

    /**
     * 发起活动（添加活动）
     *
     * @param eventModel 活动信息
     * @return 活动id
     */
    @OperateLog("发起活动")
    @DefaultActionState(value = ActionState.ADMIN, group = "event")
    @PostMapping("/info")
    public String addEvent(@RequestBody EventModel eventModel) {
        UserModel user = HttpInterceptor.userHolder.get();
        Integer eventId = eventService.addEvent(eventModel, user.getId());
        return eventId.toString();
    }

    /**
     * 修改活动
     *
     * @param eventId    活动id
     * @param eventModel 活动信息
     * @return 是否成功
     */
    @OperateLog("修改活动")
    @DefaultActionState(value = ActionState.MANAGER, group = "event")
    @PutMapping("/info")
    public String putEvent(@RequestParam @EventId Integer eventId,
                           @RequestBody EventModel eventModel) {
        if (!eventModel.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return eventService.updateEvent(eventModel).toString();
    }

    @OperateLog("获取活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/list")
    public PageModel<EventModel> getEvents(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEvents(page, size);
    }

    @OperateLog("筛选获取课表")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/list")
    public List<EventModel> postForEvents(@RequestParam(required = false) List<Integer> typeId,
                                          @RequestParam(required = false) List<Integer> departmentId,
                                          @RequestParam(required = false) String time) {
        return eventService.postForEvents(typeId, departmentId, time);
    }

    @OperateLog("获取全部组织部门(filter)")
    @DefaultActionState(value = ActionState.PUBLIC, group = "event")
    @GetMapping("/departments")
    public List<Department> getDepartments() {
        // TODO: 屏蔽部分部门
        return departmentService.getDepartments();
    }
}
