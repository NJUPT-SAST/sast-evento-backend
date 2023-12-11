package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.UserPassword;

@Mapper
@Repository
public interface UserPasswordMapper extends BaseMapper<UserPassword> {
}
