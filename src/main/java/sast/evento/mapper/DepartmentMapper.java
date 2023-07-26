package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sast.evento.entitiy.Department;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/26 19:51
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
}
