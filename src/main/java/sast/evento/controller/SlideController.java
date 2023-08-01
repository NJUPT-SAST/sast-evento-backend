package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.entitiy.Slide;

import java.util.List;

@Controller
@RequestMapping("/slide")
public class SlideController {

    @OperateLog("添加幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/info")
    public Integer addSlide(MultipartFile file,
                            @RequestParam String link,
                            @RequestParam String title) {
        return null;
    }

    @OperateLog("删除幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/info")
    public String deleteSlide(@RequestParam Integer slideId) {
        return null;
    }

    @OperateLog("编辑幻灯片")
    @DefaultActionState(ActionState.ADMIN)
    @PatchMapping("/info")
    public String patchSlide(@RequestParam Integer slideId,
                             @RequestParam(required = false) MultipartFile file,
                             @RequestParam(required = false) String link,
                             @RequestParam(required = false) String title) {
        return null;
    }

    @OperateLog("获取幻灯片列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/list")
    public List<Slide> getSlides(@RequestParam(required = false) Integer size) {
        /* 获取最新幻灯片，按id降序排列 */
        return null;
    }

}
