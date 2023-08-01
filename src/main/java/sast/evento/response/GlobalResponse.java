package sast.evento.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sast.evento.common.enums.ErrorEnum;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@Data
@ToString
@NoArgsConstructor
public class GlobalResponse<T> {

    private boolean success;
    private Integer errCode;
    private String errMsg;
    private T data;

    public static <T> GlobalResponse<T> success() {
        return success(null);
    }

    public static <T> GlobalResponse<T> success(T data) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setSuccess(true);
        response.setErrCode(null);
        response.setErrMsg(null);
        response.setData(data);
        return response;
    }

    public static <T> GlobalResponse<T> failure() {
        return failure(ErrorEnum.COMMON_ERROR);
    }

    public static <T> GlobalResponse<T> failure(ErrorEnum errorEnum) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setSuccess(false);
        response.setErrCode(errorEnum.getErrCode());
        response.setErrMsg(errorEnum.getErrMsg());
        response.setData(null);
        return response;
    }

    public static <T> GlobalResponse<T> failure(String message) {
        GlobalResponse<T> response = new GlobalResponse<>();
        response.setSuccess(false);
        response.setErrCode(ErrorEnum.COMMON_ERROR.getErrCode());
        response.setErrMsg(message);
        response.setData(null);
        return response;
    }
}
