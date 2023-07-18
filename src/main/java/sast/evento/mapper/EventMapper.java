package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.dataobject.Event;

@Mapper
@Repository
public interface EventMapper extends BaseMapper<Event> {
}
