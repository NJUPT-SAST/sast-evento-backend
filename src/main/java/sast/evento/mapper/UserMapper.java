package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sast.evento.entitiy.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
