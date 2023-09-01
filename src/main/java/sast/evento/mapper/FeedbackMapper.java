package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Feedback;

import java.util.List;
import java.util.Map;

import sast.evento.model.FeedbacksDTO;

@Mapper
@Repository
public interface FeedbackMapper extends BaseMapper<Feedback> {
    /**
     * @param index 页数
     * @param size  每页显示的数量
     * @return Map<Integer, Integer>
     * @author Aiden
     */
    @MapKey("event_id")
    List<Map<String, Integer>> getFeedbackEvents(@Param("index") Integer index, @Param("size") Integer size);

    /**
     * @param eventId 活动id
     * @return FeedbacksDTO
     * @author Aiden
     */
    FeedbacksDTO getFeedback(@Param("event_id") Integer eventId);


    Integer addFeedback(@Param("userId") String userId, @Param("content") String content, @Param("score") Integer score, @Param("eventId") Integer eventId);

}
