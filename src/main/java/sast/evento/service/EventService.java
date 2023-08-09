package sast.evento.service;

import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;

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
    List<EventModel> getHistory(UserProFile userProFile);

    // 查看所有正在进行的活动列表
    List<EventModel> getConducting();

    // 查看最新活动列表
    List<EventModel> getNewest();

    // 获取活动列表
    List<EventModel> getEvents(Integer page, Integer size);

}
