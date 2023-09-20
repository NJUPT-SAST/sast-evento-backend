package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.entitiy.User;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.FeedbackModel;
import sast.evento.model.FeedbackNumModel;
import sast.evento.model.FeedbacksDTO;
import sast.evento.model.PageModel;
import sast.evento.service.FeedBackService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Resource
    private FeedBackService feedBackService;

    /**
     * @param eventId 活动id
     * @return FeedbacksDTO
     * @author Aiden
     */
    @OperateLog("获取某活动反馈详情")
    @DefaultActionState(value = ActionState.ADMIN,group = "feedback")
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
    @OperateLog("获取活动及其反馈数量列表")
    @DefaultActionState(value = ActionState.ADMIN,group = "feedback")
    @GetMapping("/num")
    public PageModel<FeedbackNumModel> getFeedbackEvents(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return feedBackService.getFeedbackEvents(page, size);
    }

    @OperateLog("用户添加反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PostMapping("/info")
    public String addFeedback(@RequestParam(required = false) String content,
                              @RequestParam Double score,
                              @RequestParam Integer eventId) {
        User user = HttpInterceptor.userHolder.get();
        return feedBackService.addFeedback(user.getUserId(), content, score, eventId);
    }

    @OperateLog("用户自己的获取反馈列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/user/list")
    public List<FeedbackModel> getListByUserId() {
        User user = HttpInterceptor.userHolder.get();
        return feedBackService.getListByUserId(user.getUserId());
    }

    // 如果返回的是 null，那么表示用户没有反馈这个活动。
    @OperateLog("用户获取自己写的某活动的反馈详情（可判断是否反馈）")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/user/info")
    public FeedbackModel getUserFeedback(@RequestParam Integer eventId) {
        User user = HttpInterceptor.userHolder.get();
        return feedBackService.getFeedback(user.getUserId(), eventId);
    }

    // 如果传进来的 content 为空，则清空数据库的 content 字段。（考虑到有人可能想清空反馈内容，所以这样设计）
    // score 为五分制，一位小数。如果传进来的为空，则不做修改。
    @OperateLog("用户修改反馈")
    @DefaultActionState(ActionState.LOGIN)
    @PatchMapping("/info")
    public String patchFeedback(@RequestParam(required = false) String content,
                                @RequestParam(required = false) Double score,
                                @RequestParam Integer feedbackId) {
        User user = HttpInterceptor.userHolder.get();
        return feedBackService.patchFeedback(user.getUserId(), feedbackId, content, score);
    }

    @OperateLog("用户删除反馈")
    @DefaultActionState(ActionState.LOGIN)
    @DeleteMapping("/info")
    public String deleteFeedback(@RequestParam Integer feedbackId) {
        User user = HttpInterceptor.userHolder.get();
        return feedBackService.deleteFeedback(user.getUserId(), feedbackId);
    }

    @OperateLog("获取活动反馈列表（该活动的所有人的反馈）")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/list")
    public List<FeedbackModel> getListByEventId(@RequestParam Integer eventId) {
        return feedBackService.getListByEventId(eventId);
    }

}
