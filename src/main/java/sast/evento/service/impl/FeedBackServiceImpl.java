package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.mapper.FeedbackMapper;
import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.service.FeedBackService;

import java.util.List;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/10 22:51
 */
@Service
public class FeedBackServiceImpl implements FeedBackService {
    @Resource
    private FeedbackMapper feedbackMapper;

    // 管理获取活动反馈详情
    @Override
    public FeedbacksDTO getFeedback(Integer eventId) {
        FeedbacksDTO feedbacksDTO = feedbackMapper.getFeedback(eventId);
        feedbacksDTO.setEventId(eventId);
        return feedbacksDTO;
    }

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

    // 管理端获取活动及其反馈数量列表
    @Override
    public List<Map<String, Integer>> getFeedbackEvents(Integer page, Integer size) {
        Integer index = (page - 1) * size;
        return feedbackMapper.getFeedbackEvents(index, size);
    }
}
