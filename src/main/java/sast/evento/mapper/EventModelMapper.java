package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.model.EventModel;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 17:46
 */
@Mapper
@Repository
public interface EventModelMapper extends BaseMapper<EventModel> {

    EventModel getById(@Param("eventId") Integer eventId);

    // 正在进行的活动列表
    List<EventModel> getConducting();

    // 查看用户历史活动列表（参加过已结束）
    List<EventModel> getHistory(@Param("userId") Integer userId);

    // 最新的活动列表（按开始时间正序排列未开始的活动）
    List<EventModel> getNewest();

    // 获取活动列表(分页）
    List<EventModel> getEvents(@Param("index") Integer index, @Param("size") Integer size);

}