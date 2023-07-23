package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Participate;
@Mapper
@Repository
public interface ParticipateMapper extends BaseMapper<Participate> {
}
