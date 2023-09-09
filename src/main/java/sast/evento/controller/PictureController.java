package sast.evento.controller;

import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.response.GlobalResponse;
import sast.evento.utils.CosUtil;
import sast.sastlink.sdk.exception.SastLinkException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/dir")
    public List<String> getDir(@RequestParam(defaultValue = "") String dir) {
        try {
            return CosUtil.getDirs(dir);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR,e.getMessage());
        }
    }

    @OperateLog("获取图片url列表")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/list")
    public List<String> getUrls(@RequestParam(defaultValue = "") String dir,
                                @RequestParam(defaultValue = "") String lastUrl,
                                @RequestParam(defaultValue = "20") Integer size) {
        try {
            return CosUtil.getURLs(dir, lastUrl, size);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR,e.getMessage());
        }
    }

    @OperateLog("添加图片")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/info")
    public String addPicture(MultipartFile picture,
                             @RequestParam(defaultValue = "") String dir) {
        if (picture == null || picture.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture file should not be empty");
        }
        try {
            return CosUtil.upload(picture, dir);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR,e.getMessage());
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR,e.getMessage());
        }
    }

    @OperateLog("删除图片")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/info")
    public String deletePicture(@RequestParam String url) {
        if (url.isEmpty()) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "picture url should not be empty");
        }
        try {
            CosUtil.delete(url);
        } catch (CosClientException e) {
            throw new LocalRunTimeException(ErrorEnum.COS_SERVICE_ERROR,e.getMessage());
        }
        return "ok";
    }
}
