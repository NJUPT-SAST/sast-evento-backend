package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Feedback;
import sast.evento.model.FeedbackModel;

import java.util.List;

@Mapper
@Repository
public interface FeedbackMapper extends BaseMapper<Feedback> {

    Integer addFeedback(@Param("userId") String userId, @Param("content") String content, @Param("score") Integer score, @Param("eventId") Integer eventId);

}
