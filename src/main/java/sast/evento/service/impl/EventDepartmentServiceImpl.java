package sast.evento.service.impl;

import jakarta.annotation.Resource;
import sast.evento.entitiy.Department;
import sast.evento.mapper.EventDepartmentMapper;
import sast.evento.service.EventDepartmentService;

import java.util.List;

/**
 * @Author: Love98
 * @Date: 8/25/2023 12:16 PM
 */
public class EventDepartmentServiceImpl implements EventDepartmentService {
    @Resource
    private EventDepartmentMapper eventDepartmentMapper;
    @Override
    public Boolean addEventDepartment(Integer eventId, Integer departmentId) {
        return null;
    }

    @Override
    public Boolean deleteEventDepartment(Integer eventId, Integer departmentId) {
        return null;
    }

    @Override
    public Boolean addEventDepartments(Integer eventId, List<Department> departmentIds) {
        return null;
    }

    @Override
    public Boolean deleteEventDepartmentsByEventId(Integer eventId) {
        return null;
    }
}
