package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.common.enums.EventState;
import sast.evento.entitiy.Event;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.*;
import sast.evento.model.EventModel;
import sast.evento.service.EventService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio; Love98
 * @date: 2023/8/8 13:44
 */
@Service
public class EventServiceImpl implements EventService {
    @Resource
    private EventModelMapper eventModelMapper;
    @Resource
    private LocationMapper locationMapper;

    @Resource
    private EventMapper eventMapper;
    // 获取活动详情
    @Override
    public EventModel getEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        EventModel eventModel = eventModelMapper.getById(eventId);
        if (eventModel == null) {
            return null;
        }

        String locationIdStr = eventModel.getLocation();
        if (locationIdStr != null && !"".equals(locationIdStr.trim())) {
            Integer locationIdInt = Integer.valueOf(locationIdStr);
            String locationName = locationMapper.getLocationName(locationIdInt);
            eventModel.setLocation(locationName);
        }
        return eventModel;
    }

    // 查看用户历史活动列表（参加过已结束）
    @Override
    public List<EventModel> getHistory(Integer userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return eventModelMapper.getHistory(userId);
    }

    // 查看所有正在进行的活动列表
    @Override
    public List<EventModel> getConducting() {
        return eventModelMapper.getConducting();
    }

    // 查看最新活动列表（按开始时间正序排列未开始的活动）
    @Override
    public List<EventModel> getNewest() {
        return eventModelMapper.getNewest();
    }

    // 获取活动列表(分页）
    @Override
    public List<EventModel> getEvents(Integer page, Integer size) {
        if (page == null || page < 0 || size == null || size < 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        Integer index = (page - 1) * size;
        return eventModelMapper.getEvents(index, size);
    }

    // 获取已订阅的活动列表
    @Override
    public List<EventModel> getSubscribed(Integer userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return eventModelMapper.getSubscribed(userId);
    }

    @Override
    public Integer addEvent(Event event) {
        if (event == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        /* 检测必需参数是否存在 */
        if (
                (event.getTitle() == null) ||
                (event.getGmtEventStart() == null) ||
                (event.getGmtEventEnd() == null) ||
                (event.getGmtRegistrationStart() == null) ||
                (event.getGmtRegistrationEnd() == null)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "title or time should be null.");
        }
        if (!timeCheck(event)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid time.");
        }
        /* 检测 null 参数是否存在 */
        if (event.getDescription() == null) {
            event.setDescription("");
        }
        if (event.getLocationId() == null) {
            event.setLocationId(0);
        }
        if (event.getTypeId() == null) {
            event.setTypeId(0);
        }
        if (event.getTag() == null) {
            event.setTag("");
        }
        /* 设置为未开始 */
        event.setState(EventState.NOT_STARTED);
        if (eventMapper.insert(event) > 0) {
            return event.getId();
        }
        throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR);
    }

    @Override
    public Boolean deleteEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return eventMapper.deleteById(eventId) > 0;
    }

    @Override
    public Boolean updateEvent(Event event) {
        if (event == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (!timeCheck(event)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid time.");
        }
        UpdateWrapper<Event> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", event.getId());
        return eventMapper.update(event, updateWrapper) > 0;
    }


    @Override
    public Boolean cancelEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.CANCELED);
        return eventMapper.updateById(event) > 0;
    }

    private Boolean timeCheck(Event event) {
        return !event.getGmtEventStart().after(event.getGmtEventEnd()) && !event.getGmtRegistrationStart().after(event.getGmtRegistrationEnd());
    }
}
