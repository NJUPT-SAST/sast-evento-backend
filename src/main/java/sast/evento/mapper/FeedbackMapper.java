package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Feedback;

@Mapper
@Repository
public interface FeedbackMapper extends BaseMapper<Feedback> {
}
