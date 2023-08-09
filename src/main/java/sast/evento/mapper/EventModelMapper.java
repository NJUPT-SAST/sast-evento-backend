package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.model.EventModel;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 17:46
 */
@Mapper
@Repository
public interface EventModelMapper extends BaseMapper<EventModel> {

    EventModel selectEventModel(@Param("eventId") Integer eventId);

}