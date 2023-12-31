package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.EventDepartment;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.DepartmentMapper;
import sast.evento.mapper.EventDepartmentMapper;
import sast.evento.service.EventDepartmentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Love98
 * @Date: 8/25/2023 12:16 PM
 */
@Service
public class EventDepartmentServiceImpl implements EventDepartmentService {
    @Resource
    private EventDepartmentMapper eventDepartmentMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public Boolean addEventDepartment(Integer eventId, Integer departmentId) {
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("id", departmentId);
        if (!departmentMapper.exists(departmentQueryWrapper)) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "departmentId is not exist");
        }
        EventDepartment eventDepartment = new EventDepartment();
        eventDepartment.setDepartmentId(departmentId);
        eventDepartment.setEventId(eventId);
        return eventDepartmentMapper.insert(eventDepartment) > 0;
    }

    @Override
    public Boolean deleteEventDepartment(Integer id) {
        return eventDepartmentMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean addEventDepartments(Integer eventId, List<Department> departmentIds) {
        List<Integer> departmentIdList = departmentIds.stream().collect(
                ArrayList::new,
                (list, department) -> list.add(department.getId()),
                ArrayList::addAll);
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.in("id", departmentIdList);
        if (departmentMapper.selectCount(departmentQueryWrapper) != departmentIds.size()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "departmentId is not exist");
        }
        List<EventDepartment> eventDepartments = departmentIds.stream().collect(
                ArrayList::new,
                (list, department) -> {
                    EventDepartment eventDepartment = new EventDepartment();
                    eventDepartment.setDepartmentId(department.getId());
                    eventDepartment.setEventId(eventId);
                    list.add(eventDepartment);
                },
                ArrayList::addAll);
        eventDepartmentMapper.insertBatch(eventDepartments);
        return true;
    }

    @Override
    public Boolean deleteEventDepartmentsByEventId(Integer eventId) {
        QueryWrapper<EventDepartment> eventDepartmentQueryWrapper = new QueryWrapper<>();
        eventDepartmentQueryWrapper.eq("event_id", eventId);
        return eventDepartmentMapper.delete(eventDepartmentQueryWrapper) > 0;
    }

    @Override
    public Map<Integer, List<Department>> getEventDepartmentListMap(List<Integer> eventIds) {
        List<EventDepartment> eventDepartments = eventDepartmentMapper.selectBatchDepartmentByEventIds(eventIds);
        Map<Integer, List<Department>> res = new HashMap<>();
        Map<Integer, Department> departmentMap = new HashMap<>();
        for (EventDepartment eventDepartment : eventDepartments) {
            List<Department> departments = res.computeIfAbsent(eventDepartment.getEventId(), k -> new ArrayList<>());
            Department department = departmentMap.computeIfAbsent(eventDepartment.getDepartmentId(), k -> new Department(k, eventDepartment.getDepartmentName()));
            departments.add(department);
        }
        return res;
    }
}
