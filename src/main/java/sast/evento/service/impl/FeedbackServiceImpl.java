package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Feedback;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.FeedbackMapper;
import sast.evento.mapper.FeedbackModelMapper;
import sast.evento.model.FeedbackModel;
import sast.evento.service.FeedbackService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:51
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Resource
    private FeedbackMapper feedbackMapper;
    @Resource
    private FeedbackModelMapper feedbackModelMapper;

    // 用户添加反馈
    @Override
    public String addFeedback(Integer userId, String content, Double scoreDou, Integer eventId) {
        if (userId == null || eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (scoreDou == null || scoreDou < 0 || scoreDou > 5) {
            return "分数不正确，请输入 0~5 之间的数字";
        }

        // 将 score 扩大十倍并转化为 Integer 存入数据库
        Integer scoreInt = (int) (scoreDou * 10);
        Integer addResult = feedbackMapper.addFeedback(userId, content, scoreInt, eventId);
        return addResult != null && addResult > 0 ? "添加反馈成功" : "添加反馈失败";
    }

    // 用户获取反馈列表
    @Override
    public List<FeedbackModel> getListByUserId(Integer userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return feedbackModelMapper.getListByUserId(userId);
    }

    /*
     * 用户修改反馈
     * 如果传进来的 content 为空，则清空数据库的 content 字段。（考虑到有人可能想清空反馈内容，所以这样设计）
     * score 为五分制，一位小数。如果传进来的为空，则不做修改。
     */
    @Override
    public String patchFeedback(Integer userId, Integer feedbackId, String content, Double scoreDou) {
        if (userId == null || feedbackId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        Feedback feedback = new Feedback();
        feedback.setContent(content);
        if (scoreDou != null) {
            if (scoreDou < 0 || scoreDou > 5) {
                return "分数不正确，请输入 0~5 之间的数字";
            }
            // 将 score 扩大十倍并转化为 Integer 存入数据库
            Integer scoreInt = (int)(scoreDou * 10);
            feedback.setScore(scoreInt);
        }

        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", feedbackId);

        Integer updateResult = feedbackMapper.update(feedback, queryWrapper);
        return updateResult != null && updateResult > 0 ? "修改反馈成功" : "修改反馈失败";
    }

    // 用户删除反馈
    @Override
    public String deleteFeedback(Integer userId, Integer feedbackId) {
        if (userId == null || feedbackId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", feedbackId);

        Integer deleteResult = feedbackMapper.delete(queryWrapper);
        return deleteResult != null && deleteResult > 0 ? "删除反馈成功" : "删除反馈失败";
    }

    // 获取活动反馈列表（该活动的所有反馈）
    @Override
    public List<FeedbackModel> getListByEventId(Integer eventId) {
        if (eventId == null) {
            return null;
        }
        return feedbackModelMapper.getListByEventId(eventId);
    }
}
