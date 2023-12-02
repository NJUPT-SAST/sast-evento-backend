package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Permission;
import sast.evento.entitiy.User;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

}
