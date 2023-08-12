package sast.evento.service;

import sast.evento.model.FeedbackModel;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:50
 */
public interface FeedBackService {

    // 用户添加反馈
    String addFeedback(String content, Integer score, Integer eventId);

    // 用户获取反馈列表
    List<FeedbackModel> getFeedbacks();

    // 用户修改反馈
    String patchFeedback(String content, Integer score, Integer feedbackId);

    // 用户删除反馈
    String deleteFeedback(Integer feedbackId);

}
