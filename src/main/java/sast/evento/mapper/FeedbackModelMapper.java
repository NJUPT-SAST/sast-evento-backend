package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.model.FeedbackModel;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/13 11:06
 */
@Mapper
@Repository
public interface FeedbackModelMapper {

    List<FeedbackModel> getFeedbacks(@Param("userId") Integer userId);

}
