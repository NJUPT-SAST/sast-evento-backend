package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Participate;

import java.util.List;

@Mapper
@Repository
public interface ParticipateMapper extends BaseMapper<Participate> {
    List<String> selectSubscribeOpenIds(@Param("event_id") Integer eventId);

}
