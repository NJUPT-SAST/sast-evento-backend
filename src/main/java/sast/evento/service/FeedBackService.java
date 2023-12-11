package sast.evento.service;

import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbackNumModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.PageModel;

import java.util.List;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:50
 */
public interface FeedBackService {

    // 管理获取活动反馈详情
    FeedbacksDTO getFeedback(Integer eventId);

    // 用户添加反馈
    String addFeedback(String userId, String content, Double score, Integer eventId);

    // 用户获取自己的反馈列表
    List<FeedbackModel> getListByUserId(String userId);

    // 用户获取自己的对于某活动的反馈详情（可判断是否反馈）
    FeedbackModel getFeedback(String userId, Integer eventId);

    // 用户修改反馈

    String patchFeedback(String userId, Integer feedbackId, String content, Double score);

    // 用户删除反馈
    String deleteFeedback(String userId, Integer feedbackId);

    // 获取活动反馈列表（该活动的所有人的反馈）
    List<FeedbackModel> getListByEventId(Integer eventId);

    // 管理端获取活动及其反馈数量列表
    PageModel<FeedbackNumModel> getFeedbackEvents(Integer page, Integer size);

}