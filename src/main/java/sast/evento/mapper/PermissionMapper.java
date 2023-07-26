package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Permission;

@Mapper
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {
}
