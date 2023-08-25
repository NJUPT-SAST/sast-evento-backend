package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.UserProFile;
import sast.evento.service.FeedBackService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Resource
    private FeedBackService feedbackService;

    @Resource
    private FeedBackService feedBackService;

    /**
     * @param eventId 活动id
     * @return FeedbacksDTO
     * @author Aiden
     */
    @OperateLog("管理获取活动反馈详情")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/event")
    public FeedbacksDTO getFeedback(@RequestParam @EventId Integer eventId) {
        return feedBackService.getFeedback(eventId);
    }

    /**
     * @param page 第几页
     * @param size 每页显示的数量
     * @return List<Map < String, Integer>>
     * @author Aiden
     */
    @OperateLog("获取活动及其反馈数量列表（分页查询）")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/num")
    public List<Map<String, Integer>> getFeedbackEvents(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return feedBackService.getFeedbackEvents(page, size);
    }

    /**
     *
     */
    @OperateLog("用户添加反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PostMapping("/info")
    public String addFeedback(@RequestParam(required = false) String content,
                              @RequestParam Integer score,
                              @RequestParam Integer eventId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return null;
    }

    /**
     *
     */
    @OperateLog("用户获取反馈列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/info")
    public List<FeedbackModel> getFeedbacks() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

    /**
     *
     */
    @OperateLog("用户修改反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PatchMapping("/info")
    public String patchFeedback(@RequestParam(required = false) String content,
                                @RequestParam(required = false) Integer score,
                                @RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

    /**
     *
     */
    @OperateLog("用户删除反馈")
    @DefaultActionState(ActionState.LOGIN)
    @DeleteMapping("/info")
    public String deleteFeedback(@RequestParam Integer feedbackId) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }
}
