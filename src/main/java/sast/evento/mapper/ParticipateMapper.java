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

    // 订阅活动 / 取消订阅
    Integer subscribe(@Param("userId") Integer userId, @Param("eventId") Integer eventId, @Param("isSubscribe") Boolean isSubscribe);

    // 报名活动
    Integer register(@Param("userId") Integer userId, @Param("eventId") Integer eventId);
}
