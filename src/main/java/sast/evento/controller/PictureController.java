package sast.evento.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.utils.CosUtil;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/7 22:24
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @OperateLog("获取所有目录")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/dir")
    public List<String> getDir(@RequestParam(defaultValue = "") String dir) {
        return CosUtil.getDirs(dir);
    }

    @OperateLog("获取图片url列表")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/list")
    public List<String> getUrls(@RequestParam(defaultValue = "") String dir,
                                @RequestParam(defaultValue = "") String lastUrl,
                                @RequestParam(defaultValue = "20") Integer size) {
        return CosUtil.getURLs(dir, lastUrl, size);
    }

    @OperateLog("添加图片")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/info")
    public String addPicture(MultipartFile picture,
                             @RequestParam(defaultValue = "") String dir) {
        if (picture.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture file should not be empty");
        }
        return CosUtil.upload(picture, dir);
    }

    @OperateLog("删除图片")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/info")
    public String deletePicture(@RequestParam String url) {
        if (url.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture url should not be empty");
        }
        CosUtil.delete(url);
        return "ok";
    }

}
