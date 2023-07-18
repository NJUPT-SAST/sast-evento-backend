package sast.evento.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sast.evento.enums.ErrorEnum;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocalRunTimeException extends RuntimeException {

    private ErrorEnum errorEnum;

    public LocalRunTimeException(String message) {
        super(message);
    }

    public LocalRunTimeException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.errorEnum = errorEnum;
    }
    public LocalRunTimeException(ErrorEnum errorEnum,String message) {
        super(errorEnum.getErrMsg()+", "+message);
        this.errorEnum = errorEnum;
    }
}
