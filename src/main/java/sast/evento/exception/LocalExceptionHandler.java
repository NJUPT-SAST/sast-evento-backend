package sast.evento.exception;


import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.response.GlobalResponse;

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
}
