package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.mapper.FeedbackModelMapper;
import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.UserProFile;
import sast.evento.service.FeedbackService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Resource
    private FeedbackService feedbackService;

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

    /**
     */
    @OperateLog("用户添加反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PostMapping("/info")
    public String addFeedback(@RequestParam(required = false) String content,
                               @RequestParam Double score,
                               @RequestParam Integer eventId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return feedbackService.addFeedback(userIdInt, content, score, eventId);
    }

    /**
     */
    @OperateLog("用户获取反馈列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/user/info")
    public List<FeedbackModel> getListByUserId() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return feedbackService.getListByUserId(userIdInt);
    }

    /**
     */
    /*
     * 如果传进来的 content 为空，则清空数据库的 content 字段。（考虑到有人可能想清空反馈内容，所以这样设计）
     * score 为五分制，一位小数。如果传进来的为空，则不做修改。
     */
    @OperateLog("用户修改反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PatchMapping("/info")
    public String patchFeedback(@RequestParam(required = false) String content,
                                @RequestParam(required = false) Double score,
                                @RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return feedbackService.patchFeedback(userIdInt, feedbackId, content, score);
    }

    /**
     */
    @OperateLog("用户删除反馈")
    @DefaultActionState(ActionState.LOGIN)
    @DeleteMapping("/info")
    public String deleteFeedback(@RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return feedbackService.deleteFeedback(userIdInt, feedbackId);
    }

    /**
     */
    @OperateLog("获取活动反馈列表（该活动的所有人的反馈）")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/list")
    public List<FeedbackModel> getListByEventId(@RequestParam Integer eventId) {
        return feedbackService.getListByEventId(eventId);
    }
}
