package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import sast.evento.entitiy.Department;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.UserDepartmentSubscribe;

import java.util.List;

@Mapper
@Repository
public interface SubscribeDepartmentMapper extends BaseMapper<UserDepartmentSubscribe> {
    List<UserDepartmentSubscribe> selectSubscribeDepartmentUser(List<Integer> departmentIds);

    List<Department> selectSubscribeDepartment(String userId);
}
