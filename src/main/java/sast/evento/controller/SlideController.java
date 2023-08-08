package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.entitiy.Slide;
import sast.evento.service.SlideService;

import java.util.List;

@RestController
@RequestMapping("/slide")
public class SlideController {
    @Resource
    private SlideService slideService;

    @OperateLog("获取活动幻灯片列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/event/list")
    public List<Slide> getEventSlides(@RequestParam @EventId Integer eventId) {
        /* 获取所有活动幻灯片 */
        return slideService.getEventSlides(eventId);
    }

    @OperateLog("获取首页幻灯片列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/home/list")
    public List<Slide> getHomeSlides(@RequestParam(defaultValue = "3", required = false) Integer size) {
        /* 获取最新幻灯片，按id降序排列 */
        return slideService.getHomeSlides(size);
    }

    @OperateLog("添加活动幻灯片")
    @DefaultActionState(ActionState.MANAGER)
    @PostMapping("/event/info")
    public Integer addEventSlide(@RequestParam @EventId Integer eventId,
                                 @RequestParam String url,
                                 @RequestParam(required = false) String link,
                                 @RequestParam String title) {
        return slideService.addEventSlide(eventId, url, link, title);
    }

    @OperateLog("删除活动幻灯片")
    @DefaultActionState(ActionState.MANAGER)
    @DeleteMapping("/event/info")
    public String deleteEventSlide(@RequestParam @EventId Integer eventId,
                                   @RequestParam Integer slideId) {
        slideService.deleteEventSlide(slideId,eventId);
        return "ok";
    }

    @OperateLog("编辑活动幻灯片")
    @DefaultActionState(ActionState.MANAGER)
    @PatchMapping("/event/info")
    public String patchEventSlide(@RequestParam @EventId Integer eventId,
                                  @RequestParam Integer slideId,
                                  @RequestParam(required = false) String url,
                                  @RequestParam(required = false) String link,
                                  @RequestParam(required = false) String title) {
        slideService.patchEventSlide(eventId, slideId, url, link, title);
        return "ok";
    }

    @OperateLog("添加首页幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/home/info")
    public Integer addHomeSlide(@RequestParam String url,
                                @RequestParam String link,
                                @RequestParam String title) {
        return slideService.addHomeSlide(url, link, title);
    }

    @OperateLog("删除首页幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/home/info")
    public String deleteHomeSlide(@RequestParam Integer slideId) {
        slideService.deleteHomeSlide(slideId);
        return "ok";
    }

    @OperateLog("编辑首页幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @PatchMapping("/home/info")
    public String patchHomeSlide(@RequestParam Integer slideId,
                                 @RequestParam(required = false) String url,
                                 @RequestParam(required = false) String link,
                                 @RequestParam(required = false) String title) {
        slideService.patchHomeSlide(slideId, url, link, title);
        return "ok";
    }


}
