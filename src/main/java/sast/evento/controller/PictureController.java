package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.User;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.service.ImageService;

import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/7 22:24
 */
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private ImageService imageService;

    @OperateLog("获取图片url列表")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @GetMapping("/list")
    public Map<String, Object> getUrls(@RequestParam(defaultValue = "1") Integer num,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return imageService.getPictures(num, size);
    }

    @OperateLog("添加图片")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @PostMapping("/info")
    public String addPicture(MultipartFile picture) {
        User user = HttpInterceptor.userHolder.get();
        if (picture == null || picture.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture file should not be empty");
        }
        return imageService.upload(picture, user);
    }

    @OperateLog("删除图片")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @DeleteMapping("/info")
    public String deletePicture(@RequestParam String key) {
        if (key.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture key should not be empty");
        }
        imageService.deletePicture(key);
        return "ok";
    }
}
