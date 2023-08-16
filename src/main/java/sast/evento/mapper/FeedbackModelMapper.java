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

    // 用户获取反馈列表
    List<FeedbackModel> getListByUserId(@Param("userId") Integer userId);

    // 获取活动反馈列表（该活动的所有反馈）
    List<FeedbackModel> getListByEventId(@Param("eventId") Integer eventId);

}
