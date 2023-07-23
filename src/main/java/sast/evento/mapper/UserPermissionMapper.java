package sast.evento.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.UserPermission;


/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 19:21
 */
@Mapper
@Repository
public interface UserPermissionMapper extends BaseMapper<UserPermission> {
}
