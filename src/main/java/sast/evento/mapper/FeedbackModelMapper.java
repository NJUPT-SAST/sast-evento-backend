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

    // 用户获取自己的反馈列表
    List<FeedbackModel> getListByUserId(@Param("userId") String userId);

    // 获取活动反馈列表（该活动的所有人的反馈）
    List<FeedbackModel> getListByEventId(@Param("eventId") Integer eventId);

    // 用户获取自己的对于某活动的反馈详情（可判断是否反馈）
    FeedbackModel getFeedback(@Param("userId") String userId, @Param("eventId") Integer eventId);

}
