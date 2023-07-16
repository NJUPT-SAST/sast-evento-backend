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
    COMMON_ERROR(1000, "错误"),
    METHOD_NOT_EXIST(1001, "方法不存在"),
    TOKEN_ERROR(1002,"TOKEN解析错误"),
    PERMISSION_ERROR(1003,"权限错误")
    ;
    private final Integer errCode;
    private final String errMsg;
}
