package sast.evento.service;

import sast.evento.entitiy.Department;

import java.util.List;

public interface DepartmentService {
    Integer addDepartment(String departmentName);

    void deleteDepartment(Integer departmentId);

    List<Department> getDepartments();

    void putDepartment(Integer departmentId, String departmentName);
}
