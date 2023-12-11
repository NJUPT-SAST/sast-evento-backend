package sast.evento.service;

import sast.evento.entitiy.Department;
import sast.evento.entitiy.UserDepartmentSubscribe;

import java.util.List;

public interface DepartmentService {
    Integer addDepartment(String departmentName);

    void deleteDepartment(Integer departmentId);

    List<Department> getDepartments();

    void putDepartment(Integer departmentId, String departmentName);

    void subscribeDepartment(String userId, Integer departmentId, boolean insert);

    List<Department> getSubscribeDepartment(String userId);

    List<UserDepartmentSubscribe> getSubscribeDepartmentUser(List<Integer> departmentIds);

}
