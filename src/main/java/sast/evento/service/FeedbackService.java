package sast.evento.service;

import sast.evento.model.FeedbackModel;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:50
 */
public interface FeedbackService {

    // 用户添加反馈
    String addFeedback(Integer userId, String content, Double score, Integer eventId);

    // 用户获取反馈列表
    List<FeedbackModel> getFeedbacks(Integer userId);

    // 用户修改反馈

    String patchFeedback(Integer userId, Integer feedbackId, String content, Double score);

    // 用户删除反馈
    String deleteFeedback(Integer userId, Integer feedbackId);

}
