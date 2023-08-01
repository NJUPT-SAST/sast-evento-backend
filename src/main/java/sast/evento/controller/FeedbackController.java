package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.UserProFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @OperateLog("获取活动反馈详情")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/event")
    public FeedbacksDTO getFeedbacks(@RequestParam @EventId Integer eventId) {
        return null;
    }

    @OperateLog("获取活动及其反馈数量列表")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/num")
    public Map<Integer, Integer> getFeedbackEvents() {
        /* 返回一个eventId和feedbackSize对应的map */
        return null;
    }

    @OperateLog("用户添加反馈")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/info")
    public Integer addFeedback(@RequestParam(required = false) String content,
                              @RequestParam Integer score,
                              @RequestParam Integer eventId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

    @OperateLog("用户获取反馈")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/info")
    public List<FeedbackModel> getFeedback() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

    @OperateLog("用户修改反馈")
    @DefaultActionState(ActionState.PUBLIC)
    @PatchMapping("/info")
    public String patchFeedback(@RequestParam(required = false) String content,
                                @RequestParam(required = false) Integer score,
                                @RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

    @OperateLog("用户删除反馈")
    @DefaultActionState(ActionState.PUBLIC)
    @DeleteMapping("/info")
    public String deleteFeedback(@RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }
}