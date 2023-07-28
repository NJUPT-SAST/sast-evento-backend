package sast.evento.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/12 22:04
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum {
    /* 错误 */
    COMMON_ERROR(1000, "错误"),
    /* 鉴权错误 */
    PERMISSION_ERROR(1001,"权限错误"),
    USER_ALREADY_EXIST(1002,"用户已存在"),
    TOKEN_ERROR(1003,"TOKEN解析错误"),
    METHOD_NOT_EXIST(1004, "该操作不存在"),
    /*  */











    /* 其他服务错误 */
    WX_SUBSCRIBE_ERROR(10001,"微信订阅错误"),
    SCHEDULER_ERROR(10002,"scheduler错误"),
    QRCODE_ERROR(10003,"二维码生成错误")
    ;
    private final Integer errCode;
    private final String errMsg;
}
