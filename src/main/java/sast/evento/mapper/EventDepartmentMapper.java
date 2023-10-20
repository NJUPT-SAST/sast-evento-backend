package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sast.evento.entitiy.EventDepartment;

import java.util.List;

/**
 * @Author: Love98
 * @Date: 8/25/2023 12:10 PM
 */
@Mapper
public interface EventDepartmentMapper extends BaseMapper<EventDepartment> {
    void insertBatch(@Param("list") List<EventDepartment> eventDepartments);
    List<EventDepartment> selectBatchDepartmentByEventIds(@Param("eventIds") List<Integer> eventIds);
}
