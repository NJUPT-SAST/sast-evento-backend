package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.EventDepartment;
import sast.evento.entitiy.UserDepartmentSubscribe;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.DepartmentMapper;
import sast.evento.mapper.EventDepartmentMapper;
import sast.evento.mapper.SubscribeDepartmentMapper;
import sast.evento.service.DepartmentService;

import java.util.ArrayList;
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

    @Resource
    private EventDepartmentMapper eventDepartmentMapper;

    @Resource
    private SubscribeDepartmentMapper subscribeDepartmentMapper;

    @Override
    public Integer addDepartment(String departmentName) {
        Department department = new Department(null, departmentName);
        departmentMapper.insert(department);
        return department.getId();
    }

    @Override
    public void deleteDepartment(Integer departmentId) {
        eventDepartmentMapper.delete(Wrappers.lambdaQuery(EventDepartment.class)
                .eq(EventDepartment::getDepartmentId, departmentId));
        subscribeDepartmentMapper.delete(Wrappers.lambdaQuery(UserDepartmentSubscribe.class)
                .eq(UserDepartmentSubscribe::getDepartmentId, departmentId));
        departmentMapper.deleteById(departmentId);
    }

    @Override
    public List<Department> getDepartments() {
        return departmentMapper.selectList(new LambdaQueryWrapper<Department>()
                .orderByAsc(Department::getId));
    }

    @Override
    @CacheEvict(value = "event")
    public void putDepartment(Integer departmentId, String departmentName) {
        if (departmentMapper.updateById(new Department(departmentId, departmentName)) < 1) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "update failed");
        }
    }

    @Override
    public void subscribeDepartment(String userId, Integer departmentId, boolean insert) {
        try {
            if (insert) {
                subscribeDepartmentMapper.insert(new UserDepartmentSubscribe(null, userId, departmentId, null, null));
            } else {
                subscribeDepartmentMapper.delete(new LambdaQueryWrapper<UserDepartmentSubscribe>()
                        .eq(UserDepartmentSubscribe::getUserId, userId)
                        .and(wrapper -> wrapper.eq(UserDepartmentSubscribe::getDepartmentId, departmentId)));
            }
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, (insert ? "subscribe" : "cancel") + " failed");
        }
    }

    @Override
    public List<Department> getSubscribeDepartment(String userId) {
        return subscribeDepartmentMapper.selectSubscribeDepartment(userId);
    }

    @Override
    public List<UserDepartmentSubscribe> getSubscribeDepartmentUser(List<Integer> departmentIds) {
        if (departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        return subscribeDepartmentMapper.selectSubscribeDepartmentUser(departmentIds);
    }
}
