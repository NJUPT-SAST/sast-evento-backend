package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sast.evento.entitiy.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    void ignoreInsertUser(@Param("id") String id,
                          @Param("studentId") String studentId,
                          @Param("openId") String openId,
                          @Param("email") String email);
}
