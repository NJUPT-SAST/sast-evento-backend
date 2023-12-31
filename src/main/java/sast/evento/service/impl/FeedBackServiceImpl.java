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
import sast.evento.model.FeedbackNumModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.PageModel;
import sast.evento.service.FeedBackService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:51
 */
@Service
public class FeedBackServiceImpl implements FeedBackService {
    @Resource
    private FeedbackMapper feedbackMapper;
    @Resource
    private FeedbackModelMapper feedbackModelMapper;

    // 管理获取活动反馈详情
    @Override
    public FeedbacksDTO getFeedback(Integer eventId) {
        FeedbacksDTO feedbacksDTO = feedbackMapper.getFeedback(eventId);
        if (feedbacksDTO == null) {
            throw new LocalRunTimeException(ErrorEnum.EVENT_NOT_EXIST);
        }
        feedbacksDTO.setEventId(eventId);
        return feedbacksDTO;
    }

    // 用户添加反馈
    @Override
    public String addFeedback(String userId, String content, Double scoreDou, Integer eventId) {
        if (userId == null || eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (scoreDou == null || scoreDou < 0 || scoreDou > 5) {
            return "分数不正确，请输入 0~5 之间的数字";
        }

        // 将 score 扩大十倍并转化为 Integer 存入数据库
        Integer scoreInt = (int) (scoreDou * 10);
        Integer insertResult = feedbackMapper.addFeedback(userId, content, scoreInt, eventId);
        return insertResult != null && insertResult > 0 ? "添加反馈成功" : "添加反馈失败";
    }

    // 用户获取自己的反馈列表
    @Override
    public List<FeedbackModel> getListByUserId(String userId) {
        if (userId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return feedbackModelMapper.getListByUserId(userId);
    }

    // 用户获取自己的对于某活动的反馈详情（可判断是否反馈）
    @Override
    public FeedbackModel getFeedback(String userId, Integer eventId) {
        if (userId == null || eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        return feedbackModelMapper.getFeedback(userId, eventId);
    }

    /*
     * 用户修改反馈
     * 如果传进来的 content 为空，则清空数据库的 content 字段。（考虑到有人可能想清空反馈内容，所以这样设计）
     * score 为五分制，一位小数。如果传进来的为空，则不做修改。
     */
    @Override
    public String patchFeedback(String userId, Integer feedbackId, String content, Double scoreDou) {
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
            Integer scoreInt = (int) (scoreDou * 10);
            feedback.setScore(scoreInt);
        }

        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", feedbackId);

        int updateResult = feedbackMapper.update(feedback, queryWrapper);
        return updateResult > 0 ? "修改反馈成功" : "修改反馈失败";
    }

    // 用户删除反馈
    @Override
    public String deleteFeedback(String userId, Integer feedbackId) {
        if (userId == null || feedbackId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("id", feedbackId);

        int deleteResult = feedbackMapper.delete(queryWrapper);
        return deleteResult > 0 ? "删除反馈成功" : "删除反馈失败";
    }

    // 获取活动反馈列表（该活动的所有人的反馈）
    @Override
    public List<FeedbackModel> getListByEventId(Integer eventId) {
        return feedbackModelMapper.getListByEventId(eventId);
    }

    // 管理端获取活动及其反馈数量列表
    @Override
    public PageModel<FeedbackNumModel> getFeedbackEvents(Integer page, Integer size) {
        Integer index = (page - 1) * size;
        return feedbackMapper.getFeedbackEvents(index, size);
    }
}
