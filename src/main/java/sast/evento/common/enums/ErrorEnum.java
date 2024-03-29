package sast.evento.common.enums;

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
    COMMON_ERROR(1000, "error"),
    /* 鉴权错误 */
    PERMISSION_ERROR(1001, "permission error"),
    USER_ALREADY_EXIST(1002, "user already exist"),
    TOKEN_ERROR(1003, "token decode error"),
    METHOD_NOT_EXIST(1004, "method not exist"),
    /* 参数错误 */
    PARAM_ERROR(1005, "param error"),
    /* 数据不存在 */
    EVENT_TYPE_NOT_EXIST(1006, "eventType not exist"),
    LOCATION_NOT_EXIST(1007, "location not exist"),
    EVENT_NOT_EXIST(1008, "event not exist"),
    /* 登录异常 */
    LOGIN_ERROR(1009, "login failed"),
    LOGIN_EXPIRE(1010, "login expired"),
    NOT_REGISTER(1011, "user info not exist please register first"),

    /* 其他服务错误 */
    WX_SERVICE_ERROR(10001, "wx service error"),
    WX_SUBSCRIBE_ERROR(10001, "wx subscribe error"),
    SCHEDULER_ERROR(10002, "scheduler error"),
    QRCODE_ERROR(10003, "qr code generation error"),
    COS_SERVICE_ERROR(10004, "cos service error"),
    SAST_LINK_SERVICE_ERROR(10005, "sast link service error"),
    STUDENT_NOT_BIND(10006, "bind studentId first"),
    /* 时间格式错误 */
    TIME_ERROR(20001, "time format error");
    private final Integer errCode;
    private final String errMsg;
}
