package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.common.enums.EventState;
import sast.evento.entitiy.Event;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.*;
import sast.evento.model.EventModel;
import sast.evento.service.EventDepartmentService;
import sast.evento.service.EventService;
import sast.evento.service.EventStateScheduleService;
import sast.evento.service.PermissionService;
import sast.evento.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    @Resource
    private EventTypeMapper eventTypeMapper;

    @Resource
    private EventStateScheduleService eventStateScheduleService;

    @Resource
    private EventDepartmentService eventDepartmentService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private TimeUtil timeUtil;

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

    @Transactional
    @Override
    public Integer addEvent(EventModel eventModel, String userId) {
        Event event = new Event(eventModel);
        /* 检测必需参数是否存在 */
        if (
                (event.getTitle() == null) ||
                        (event.getGmtEventStart() == null) ||
                        (event.getGmtEventEnd() == null) ||
                        (event.getGmtRegistrationStart() == null) ||
                        (event.getGmtRegistrationEnd() == null)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "title or time should be null.");
        }
        /* 时间检查 */
        if (!timeCheck(event)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid time.");
        }
        /* 检测 null 参数是否存在 */
        if (event.getDescription() == null) {
            event.setDescription("");
        }
        if (event.getLocationId() == null) {
            event.setLocationId(1);
        }
        if (event.getTypeId() == null) {
            event.setTypeId(1);
        }
        if (event.getTag() == null) {
            event.setTag("");
        }
        /* 地点检查 */
        if (!locationCheck(event)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "location not exist.");
        }
        /* 冲突判断 */
        if (!conflictCheck(event)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "conflict with other event.");
        }
        /* 设置为未开始 */
        event.setState(EventState.NOT_STARTED);
        if (eventMapper.insert(event) <= 0) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR);
        }
        addEventStateSchedule(event);
        if (eventDepartmentService.addEventDepartments(event.getId(), eventModel.getDepartments())) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "add eventDepartment failed");
        }
        String[] methods = {"addEvent", "putEvent", "patchEvent", "deleteEvent"};
        List<String> methodNames = new ArrayList<>(Arrays.asList(methods));
        permissionService.addManager(event.getId(), methodNames, userId, null);
        return event.getId();
    }

    @Transactional
    @Override
    public Boolean deleteEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (!eventDepartmentService.deleteEventDepartmentsByEventId(eventId)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete eventDepartment failed");
        }
        boolean isSuccess = eventMapper.deleteById(eventId) > 0;
        if (isSuccess) {
            eventStateScheduleService.removeJobs(eventId);
        } else {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete event failed");
        }
        return true;
    }

    @Transactional
    @Override
    public Boolean updateEvent(EventModel eventModel) {
        Event event = new Event(eventModel);

        // 检查对应的Event是否存在
        Event originEvent = eventMapper.selectById(event.getId());
        if (originEvent == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "event not exist.");
        }

        // 用于判断是否需要更新定时任务
        boolean isGmtRegistrationStartUpdated = event.getGmtRegistrationStart() != null;
        boolean isGmtRegistrationEndUpdated = event.getGmtRegistrationEnd() != null;
        boolean isGmtEventStartUpdated = event.getGmtEventStart() != null;
        boolean isGmtEventEndUpdated = event.getGmtEventEnd() != null;

        // 用于判断是否需要重新检查时间、地点、类型是否合法
        boolean isTimeUpdated = isGmtRegistrationStartUpdated || isGmtRegistrationEndUpdated || isGmtEventStartUpdated || isGmtEventEndUpdated;
        boolean isLocationUpdated = event.getLocationId() != null;
        boolean isTypeUpdated = event.getTypeId() != null;

        // 如果更新了时间、地点、类型，需要重新检查时间、地点、类型是否合法
        if (isTimeUpdated || isLocationUpdated || isTypeUpdated) {
            if (!isGmtRegistrationStartUpdated) {
                event.setGmtRegistrationStart(originEvent.getGmtRegistrationStart());
            }
            if (!isGmtRegistrationEndUpdated) {
                event.setGmtRegistrationEnd(originEvent.getGmtRegistrationEnd());
            }
            if (!isGmtEventStartUpdated) {
                event.setGmtEventStart(originEvent.getGmtEventStart());
            }
            if (!isGmtEventEndUpdated) {
                event.setGmtEventEnd(originEvent.getGmtEventEnd());
            }
            if (event.getLocationId() == null) {
                event.setLocationId(originEvent.getLocationId());
            }
            if (event.getTypeId() == null) {
                event.setTypeId(originEvent.getTypeId());
            }
            if (!timeCheck(event)) {
                throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid time.");
            }
            if (!locationCheck(event)) {
                throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "location not exist.");
            }
            if (!conflictCheck(event)) {
                throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "conflict with other event.");
            }
        }

        // 更新数据库
        UpdateWrapper<Event> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", event.getId());
        boolean isSuccess =  eventMapper.update(event, updateWrapper) > 0;

        // 添加更新任务
        if (isSuccess && isTimeUpdated) {
            if (isGmtRegistrationStartUpdated) {
                eventStateScheduleService.updateJob(event.getId(), event.getGmtRegistrationStart(), EventState.CHECKING_IN.getState());
            }
            if (isGmtRegistrationEndUpdated) {
                eventStateScheduleService.updateJob(event.getId(), event.getGmtRegistrationEnd(), EventState.NOT_STARTED.getState());
            }
            if (isGmtEventStartUpdated) {
                eventStateScheduleService.updateJob(event.getId(), event.getGmtEventStart(), EventState.IN_PROGRESS.getState());
            }
            if (isGmtEventEndUpdated) {
                eventStateScheduleService.updateJob(event.getId(), event.getGmtEventEnd(), EventState.ENDED.getState());
            }
        }
        if (eventModel.getDepartments() != null) {
            eventDepartmentService.deleteEventDepartmentsByEventId(event.getId());
            eventDepartmentService.addEventDepartments(event.getId(), eventModel.getDepartments());
        }

        return isSuccess;
    }

    @Override
    public void updateEvent(Event event) {
        UpdateWrapper<Event> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", event.getId());
        eventMapper.update(event, updateWrapper);
    }


    @Override
    public Boolean cancelEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.CANCELED);
        boolean isSuccess = eventMapper.updateById(event) > 0;
        if (isSuccess) {
            eventStateScheduleService.removeJobs(eventId);
        }
        return isSuccess;
    }

    private Boolean timeCheck(Event event) {
        Date now = new Date();
        if (event.getGmtEventStart().before(now)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "event start time should be after now.");
        }
        if (!event.getGmtEventStart().after(event.getGmtRegistrationEnd())) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "event start time should be after registration end time.");
        }
        return !event.getGmtEventStart().after(event.getGmtEventEnd()) && !event.getGmtRegistrationStart().after(event.getGmtRegistrationEnd());
    }

    private Boolean locationCheck(Event event) {
        Integer locationId = event.getLocationId();
        if (locationId == 1) {
            return true;
        }
        Location location = locationMapper.selectById(locationId);
        return location != null;
    }

    private Boolean conflictCheck(Event event) {
        Integer eventTypeId = event.getTypeId();
        if (eventTypeId == 1 || eventTypeMapper.selectById(eventTypeId).getAllowConflict() || event.getLocationId() == 1) {
            return true;
        }
        QueryWrapper<Event> eventQueryWrapper = new QueryWrapper<>();
        // 依据时间判断
        eventQueryWrapper.between("gmt_event_start", event.getGmtEventStart(), event.getGmtEventEnd())
                .or()
                .between("gmt_event_end", event.getGmtEventStart(), event.getGmtEventEnd());
        List<Event> events = eventMapper.selectList(eventQueryWrapper);
        for (Event e :
                events) {
            if (e.getLocationId().equals(event.getLocationId())) {
                return false;
            }
            if (e.getTypeId().equals(eventTypeId)) {
                // 暂时不用&&，可能需要修改：冲突优先级问题，如果一个允许冲突，一个不允许冲突，结果是冲突还是不冲突
                if (!eventTypeMapper.selectById(e.getTypeId()).getAllowConflict()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addEventStateSchedule(Event event) {
        eventStateScheduleService.scheduleJob(event.getId(), event.getGmtRegistrationStart(), EventState.CHECKING_IN.getState());
        eventStateScheduleService.scheduleJob(event.getId(), event.getGmtRegistrationEnd(), EventState.NOT_STARTED.getState());
        eventStateScheduleService.scheduleJob(event.getId(), event.getGmtEventStart(), EventState.IN_PROGRESS.getState());
        eventStateScheduleService.scheduleJob(event.getId(), event.getGmtEventEnd(), EventState.ENDED.getState());
    }

    @Override
    public List<EventModel> postForEvents(List<Integer> typeId, List<Integer> departmentId, String time) {
        if (typeId.isEmpty()) {
            if (departmentId.isEmpty()) {
                if (time.isEmpty()) {
                    return getEvents(1, 10);
                }
                List<Date> date = timeUtil.getDateOfMonday(time);
                return eventModelMapper.getEventByTime(date.get(0), date.get(1));
            }
            if (time.isEmpty()) {
                return eventModelMapper.getEventByDepartmentId(departmentId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByDepartmentIdAndTime(departmentId, date.get(0), date.get(1));
        }
        if (departmentId.isEmpty()) {
            if (time.isEmpty()) {
                return eventModelMapper.getEventByTypeId(typeId);
            }
            List<Date> date = timeUtil.getDateOfMonday(time);
            return eventModelMapper.getEventByTypeIdAndTime(typeId, date.get(0), date.get(1));
        }
        if (time.isEmpty()) {
            return eventModelMapper.getEventByTypeIdAndDepartmentId(typeId, departmentId);
        }
        List<Date> date = timeUtil.getDateOfMonday(time);
        return eventModelMapper.postForEventsByAll(typeId, departmentId, date.get(0), date.get(1));
    }

    /**
     * @param events 需要转换的地址
     * @return List<EventModel>
     * @author Aiden
     * 将活动中的location转化为要求的格式
     */
    @Override
    public List<EventModel> exchangeLocationOfEvents(List<EventModel> events) {
        // 获取所有location
        List<Location> locations = locationMapper.selectList(null);
        // 获取event中的location并转化成符合要求的结果
        Integer locationId;
        // 用于判断每个连接的字符串的后面是否需要空格，最详细的那一栏后面不需要
        Boolean isNeedSpace = false;
        StringBuilder fullAddress = new StringBuilder();
        List<EventModel> resultEvents = new ArrayList<>();
        for (EventModel event : events) {
            locationId = Integer.valueOf(event.getLocation());
            while (locationId > 0) {
                fullAddress.insert(0, locations.get(locationId - 1).getLocationName() + " ");
                if (isNeedSpace.equals(false)) {
                    fullAddress.deleteCharAt(fullAddress.length() - 1);
                    isNeedSpace = true;
                }
                locationId = locations.get(locationId - 1).getParentId();
            }
            event.setLocation(fullAddress.toString());
            resultEvents.add(event);
            fullAddress = new StringBuilder();
            isNeedSpace = false;
        }
        return resultEvents;
    }
}
