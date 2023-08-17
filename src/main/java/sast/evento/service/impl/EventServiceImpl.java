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
import sast.evento.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @Resource
    private TimeUtil timeUtil;

    // 获取活动详情
    @Override
    public EventModel getEvent(Integer eventId) {
        if (eventId == null || eventId <= 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        EventModel eventModel = eventModelMapper.getById(eventId);
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
        Integer index = (page - 1) * size;
        return eventModelMapper.getEvents(index, size);
    }

    @Override
    public List<EventModel> postForEvents(List<Integer> typeId,List<Integer> departmentId, String time){
        if(typeId.isEmpty()){
            if(departmentId.isEmpty()){
                if(time.isEmpty()){
                    return getEvents(1,10);
                }
                List<Date> date = timeUtil.getDateOfMonday(time);
                return eventModelMapper.getEventByTime(date.get(0),date.get(1));
            }
            if(time.isEmpty()){
                return eventModelMapper.getEventByDepartmentId(departmentId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByDepartmentIdAndTime(departmentId,date.get(0),date.get(1));
        }
        if(departmentId.isEmpty()){
            if(time.isEmpty()){
                return eventModelMapper.getEventByTypeId(typeId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByTypeIdAndTime(typeId,date.get(0),date.get(1));
        }
        if(time.isEmpty()){
            return eventModelMapper.getEventByTypeIdAndDepartmentId(typeId,departmentId);
        }
        List<Date> date = timeUtil.getDateOfMonday(time);
        return eventModelMapper.postForEventsByAll(typeId,departmentId,date.get(0),date.get(1));
    }

}
