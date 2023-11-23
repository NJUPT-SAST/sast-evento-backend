package sast.evento.exception;


import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.response.GlobalResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@RestControllerAdvice
public class LocalExceptionHandler {

    @ExceptionHandler(LocalRunTimeException.class)
    public <T> GlobalResponse<T> localException(LocalRunTimeException e) {
        if (e == null) {
            return GlobalResponse.failure();
        }
        ErrorEnum errorEnum = e.getErrorEnum();
        if (!e.getMessage().isEmpty()) {
            if (errorEnum != null) {
                return GlobalResponse.failure(errorEnum, e.getMessage());
            }
            return GlobalResponse.failure(e.getMessage());
        }
        return GlobalResponse.failure(errorEnum);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> GlobalResponse<T> handlerValidationException(MethodArgumentNotValidException e) {
        String messages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        return GlobalResponse.failure(messages);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public <T> GlobalResponse<T> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorEnum error = ErrorEnum.PARAM_ERROR;
        return GlobalResponse.failure(error,error.getErrMsg() + ", "+e.getParameterName()+" should not be null");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public <T> GlobalResponse<T> handlerSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        ErrorEnum error = ErrorEnum.PARAM_ERROR;
        return GlobalResponse.failure(error,error.getErrMsg() + ", id out of range or key information repeated");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public <T> GlobalResponse<T> handlerHttpServerErrorException(HttpServerErrorException e) {
        return GlobalResponse.failure(e);
    }

}
