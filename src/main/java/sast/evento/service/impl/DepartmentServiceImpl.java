package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.Department;
import sast.evento.mapper.DepartmentMapper;
import sast.evento.service.DepartmentService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/23 18:12
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public Integer addDepartment(String departmentName) {
        Department department = new Department(null,departmentName);
        departmentMapper.insert(department);
        return department.getId();
    }

    @Override
    public void deleteDepartment(Integer departmentId) {
        departmentMapper.deleteById(departmentId);
    }

    @Override
    public List<Department> getDepartments() {
        return departmentMapper.selectList(new LambdaQueryWrapper<Department>()
                .orderByAsc(Department::getId));
    }

    @Override
    public void putDepartment(Integer departmentId, String departmentName) {
        departmentMapper.updateById(new Department(departmentId,departmentName));
    }
}
