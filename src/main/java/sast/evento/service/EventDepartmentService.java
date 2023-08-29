package sast.evento.service;

import sast.evento.entitiy.Department;

import java.util.List;

/**
 * @Author: Love98
 * @Date: 8/25/2023 12:12 PM
 */
public interface EventDepartmentService {
    Boolean addEventDepartment(Integer eventId, Integer departmentId);
    Boolean deleteEventDepartment(Integer id);
    Boolean addEventDepartments(Integer eventId, List<Department> departmentIds);
    Boolean deleteEventDepartmentsByEventId(Integer eventId);
}
