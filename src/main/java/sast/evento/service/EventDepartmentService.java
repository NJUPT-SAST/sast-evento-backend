package sast.evento.service;

import org.springframework.stereotype.Service;
import sast.evento.entitiy.Department;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: Love98
 * @Date: 8/25/2023 12:12 PM
 */
public interface EventDepartmentService {
    Boolean addEventDepartment(Integer eventId, Integer departmentId);
    Boolean deleteEventDepartment(Integer id);
    Boolean addEventDepartments(Integer eventId, List<Department> departmentIds);
    Boolean deleteEventDepartmentsByEventId(Integer eventId);
    Map<Integer,List<Department>> getEventDepartmentListMap(List<Integer> eventIds);
}
