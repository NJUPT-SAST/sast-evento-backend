package sast.evento.controller;

import com.qcloud.cos.exception.CosClientException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.utils.CosUtil;

import java.util.List;
import java.util.Map;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/7 22:24
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @OperateLog("获取所有目录")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @GetMapping("/dir")
    public List<String> getDir(@RequestParam(defaultValue = "") String dir) {
        try {
            return CosUtil.getDirs(dir);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, e.getMessage());
        }
    }

    @OperateLog("获取图片url列表")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @GetMapping("/list")
    public Map<String, Object> getUrls(@RequestParam(defaultValue = "") String dir,
                                       @RequestParam(defaultValue = "1") Integer num,
                                       @RequestParam(defaultValue = "20") Integer size) {
        try {
            List<String> keys = CosUtil.getKeys(dir);
            List<String> res = CosUtil.changeKey2URL(keys.subList(num - 1, num + size - 1));
            return Map.of("pictures", CosUtil.changeKey2URL(res), "total", keys.size());
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, e.getMessage());
        }
    }

    @OperateLog("添加图片")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @PostMapping("/info")
    public String addPicture(MultipartFile picture,
                             @RequestParam(defaultValue = "") String dir) {
        if (picture == null || picture.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture file should not be empty");
        }
        try {
            return CosUtil.upload(picture, dir);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, e.getMessage());
        }
    }

    @OperateLog("删除图片")
    @DefaultActionState(value = ActionState.ADMIN, group = "picture")
    @DeleteMapping("/info")
    public String deletePicture(@RequestParam String url) {
        if (url.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture url should not be empty");
        }
        try {
            CosUtil.delete(url);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR, e.getMessage());
        }
        return "ok";
    }
}
