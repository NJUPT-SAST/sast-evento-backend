package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.common.enums.EventState;
import sast.evento.config.ActionRegister;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.Event;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.EventMapper;
import sast.evento.mapper.EventModelMapper;
import sast.evento.mapper.EventTypeMapper;
import sast.evento.mapper.LocationMapper;
import sast.evento.model.Action;
import sast.evento.model.EventModel;
import sast.evento.model.PageModel;
import sast.evento.service.*;
import sast.evento.utils.TimeUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ParticipateService participateService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private LocationService locationService;

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
        Integer locationId = eventModel.getLocationId();
        if (locationId != null) {
            String locationName = locationMapper.getLocationName(locationId);
            eventModel.setLocation(locationName);
        }
        return eventModel;
    }

    // 查看用户历史活动列表（参加过已结束）
    @Override
    public List<EventModel> getHistory(String userId) {
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
    public PageModel<EventModel> getEvents(Integer page, Integer size) {
        if (page == null || page < 0 || size == null || size < 0) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        Integer index = (page - 1) * size;
        PageModel<EventModel> res = eventModelMapper.getEvents(index, size);
        if (res == null) {
            res = new PageModel<>();
            res.setTotal(0);
            res.setResult(Collections.emptyList());
        }
        Map<Integer, String> locationNameMap = locationService.getLocationStrMap();
        res.getResult()
                .forEach(eventModel -> eventModel.setLocation(locationNameMap.get(eventModel.getLocationId())));
        List<Integer> eventIds = res.getResult().stream()
                .map(EventModel::getId).toList();
        Map<Integer, List<Department>> departmentsMap = eventDepartmentService.getEventDepartmentListMap(eventIds);
        res.getResult()
                .forEach(eventModel -> eventModel.setDepartments(departmentsMap.get(eventModel.getId())));
        return res;
    }

    // 获取已订阅的活动列表（本周和未来的活动）
    @Override
    public List<EventModel> getSubscribed(String userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        String time = timeUtil.getTime();
        List<Date> dates = timeUtil.getDateOfMonday(time);
        if (dates == null || dates.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.TIME_ERROR);
        }
        dates.set(1, timeUtil.FINAL_DATE);
        return eventModelMapper.getSubscribed(userId, dates.get(0), dates.get(1));
    }

    @Transactional(rollbackFor = Exception.class)
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
            /* 修改默认地点为root */
            event.setLocationId(0);
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
        if (!eventDepartmentService.addEventDepartments(event.getId(), eventModel.getDepartments())) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "add eventDepartment failed");
        }
        /* 为创建者添加活动全部权限 */
        List<String> methodNames = ActionRegister.actionName2action.values().stream()
                .filter(action -> action.getActionState().equals(ActionState.MANAGER))
                .map(Action::getMethodName)
                .collect(Collectors.toList());
        permissionService.addManager(event.getId(), methodNames, userId);
        return event.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteEvent(Integer eventId) {
        if (eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (!eventDepartmentService.deleteEventDepartmentsByEventId(eventId)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete eventDepartment failed");
        }
        participateService.deleteAllParticipateOfEvent(eventId);
        boolean isSuccess = eventMapper.deleteById(eventId) > 0;
        if (isSuccess) {
            eventStateScheduleService.removeJobs(eventId);
        } else {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete event failed");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
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
            // 立即更新状态
            event.setState(getMatchState(event.getGmtRegistrationStart(), event.getGmtRegistrationEnd(), event.getGmtEventStart(), event.getGmtEventEnd()));
        }

        // 更新数据库
        UpdateWrapper<Event> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", event.getId());
        boolean isSuccess = eventMapper.update(event, updateWrapper) > 0;

        // 添加更新任务
        if (isSuccess && isTimeUpdated) {
            if (isGmtRegistrationStartUpdated) {
                if (event.getGmtRegistrationStart().after(new Date())) {
                    eventStateScheduleService.updateJob(event.getId(), event.getGmtRegistrationStart(), EventState.CHECKING_IN.getState());
                } else {
                    eventStateScheduleService.removeJob(event.getId(), EventState.CHECKING_IN.getState());
                }
            }
            if (isGmtRegistrationEndUpdated) {
                if (event.getGmtRegistrationEnd().after(new Date())) {
                    eventStateScheduleService.updateJob(event.getId(), event.getGmtRegistrationEnd(), EventState.NOT_STARTED.getState());
                } else {
                    eventStateScheduleService.removeJob(event.getId(), EventState.NOT_STARTED.getState());
                }
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
        String today = timeUtil.getTime();
        List<Date> dates = timeUtil.getDateOfMonday(today);
        // 如果time不为空，则将dates起始日期改为time所设置的日期
        if (!time.isEmpty()) {
            dates.set(0, timeUtil.validTime(time).getTime());
            dates.set(1, timeUtil.FINAL_DATE);
        }
        if (typeId.isEmpty()) {
            if (departmentId.isEmpty()) {
                return eventModelMapper.getEventByTime(dates.get(0), dates.get(1));
            }
            return eventModelMapper.getEventByDepartmentIdAndTime(departmentId, dates.get(0), dates.get(1));
        }
        if (departmentId.isEmpty()) {
            return eventModelMapper.getEventByTypeIdAndTime(typeId, dates.get(0), dates.get(1));
        }
        return eventModelMapper.postForEventsByAll(typeId, departmentId, dates.get(0), dates.get(1));
    }

    // 获取已报名的活动列表（本周和未来的活动）
    @Override
    public List<EventModel> getRegistered(String userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        String time = timeUtil.getTime();
        List<Date> dates = timeUtil.getDateOfMonday(time);
        if (dates == null || dates.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.TIME_ERROR);
        }
        dates.set(1, timeUtil.FINAL_DATE);
        return eventModelMapper.getRegistered(userId, dates.get(0), dates.get(1));
    }

    private EventState getMatchState(Date registrationStart, Date registrationEnd, Date eventStart, Date eventEnd) {
        Date date = new Date();
        if (registrationStart.before(date) && registrationEnd.after(date)) {
            return EventState.CHECKING_IN;
        }
        if (eventStart.before(date) && eventEnd.after(date)) {
            return EventState.IN_PROGRESS;
        }
        if (eventEnd.before(date)) {
            return EventState.ENDED;
        }
        return EventState.NOT_STARTED;
    }

}
