package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Event;
import sast.evento.entitiy.EventType;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.*;
import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;
import sast.evento.service.EventService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 13:44
 */
@Service
public class EventServiceImpl implements EventService {
    @Resource
    private EventModelMapper eventModelMapper;
    @Resource
    private LocationMapper locationMapper;

    // 获取活动详情
    @Override
    public EventModel getEvent(Integer eventId) {
        if (eventId == null || eventId <= 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        EventModel eventModel = eventModelMapper.selectEventModel(eventId);
        if (eventModel == null) {
            return null;
        }

        String locationIdStr = eventModel.getLocation();
        if (locationIdStr != null && !"".equals(locationIdStr)) {
            Integer locationIdInt = Integer.valueOf(locationIdStr);
            String locationName = locationMapper.getLocationName(locationIdInt);
            eventModel.setLocation(locationName);
        }
        return eventModel;
    }

    // 查看用户历史活动列表（参加过已结束）
    @Override
    public List<EventModel> getHistory(UserProFile userProFile) {
        return null;
    }

    // 查看所有正在进行的活动列表
    @Override
    public List<EventModel> getConducting() {
        return null;
    }

    // 查看最新活动列表
    @Override
    public List<EventModel> getNewest() {
        return null;
    }

    // 获取活动列表
    @Override
    public List<EventModel> getEvents(Integer page, Integer size) {
        return null;
    }
}
