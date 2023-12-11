package sast.evento.service;

import sast.evento.entitiy.Event;
import sast.evento.model.EventModel;
import sast.evento.model.PageModel;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 13:03
 */
public interface EventService {

    // 获取活动详情
    EventModel getEvent(Integer eventId);

    // 查看用户历史活动列表（参加过已结束）
    List<EventModel> getHistory(String userId);

    // 查看所有正在进行的活动列表
    List<EventModel> getConducting();

    // 查看最新活动列表
    List<EventModel> getNewest();

    // 获取活动列表
    PageModel<EventModel> getEvents(Integer page, Integer size);

    // 获取已订阅的活动列表
    List<EventModel> getSubscribed(String userId);

    // 获取已报名的活动列表
    List<EventModel> getRegistered(String userId);

    // 发起活动（添加活动）
    Integer addEvent(EventModel eventModel, String userId);

    // 删除活动
    Boolean deleteEvent(Integer eventId);

    // 修改活动
    Boolean updateEvent(EventModel eventModel);

    void updateEvent(Event event);

    // 取消活动（部分修改活动信息）
    Boolean cancelEvent(Integer eventId);

    List<EventModel> postForEvents(List<Integer> typeId, List<Integer> departmentId, String time);
}
