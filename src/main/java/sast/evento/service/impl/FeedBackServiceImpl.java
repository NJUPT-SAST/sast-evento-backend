package sast.evento.service.impl;

import jakarta.annotation.Resource;
import sast.evento.entitiy.Feedback;
import sast.evento.mapper.FeedbackMapper;
import sast.evento.model.FeedbackModel;
import sast.evento.service.FeedBackService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:51
 */
public class FeedBackServiceImpl implements FeedBackService {
    @Resource
    private FeedbackMapper feedbackMapper;

    // 用户添加反馈
    @Override
    public String addFeedback(String content, Integer score, Integer eventId) {
        return null;
    }

    // 用户获取反馈列表
    @Override
    public List<FeedbackModel> getFeedbacks() {
        return null;
    }

    // 用户修改反馈
    @Override
    public String patchFeedback(String content, Integer score, Integer feedbackId) {
        return null;
    }

    // 用户删除反馈
    @Override
    public String deleteFeedback(Integer feedbackId) {
        return null;
    }
}
