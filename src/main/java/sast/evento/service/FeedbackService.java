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
    String addFeedback(String userId, String content, Double score, Integer eventId);

    // 用户获取自己的反馈列表
    List<FeedbackModel> getListByUserId(Integer userId);

    // 用户获取自己的对于某活动的反馈详情（可判断是否反馈）
    FeedbackModel getFeedback(Integer userId, Integer eventId);

    // 用户修改反馈

    String patchFeedback(Integer userId, Integer feedbackId, String content, Double score);

    // 用户删除反馈
    String deleteFeedback(Integer userId, Integer feedbackId);

    // 获取活动反馈列表（该活动的所有人的反馈）
    List<FeedbackModel> getListByEventId(Integer eventId);

}
