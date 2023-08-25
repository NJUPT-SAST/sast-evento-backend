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
    List<User> getUserHasPermissionByEvent(Integer eventId);

    List<Integer> getManageEvent(String userId);

    void updatePermission(@Param("user_id") String userId,
                          @Param("event_id") Integer eventId,
                          @Param("all_method_name") String allMethodName,
                          @Param("update_time") Date updateTime);

}
