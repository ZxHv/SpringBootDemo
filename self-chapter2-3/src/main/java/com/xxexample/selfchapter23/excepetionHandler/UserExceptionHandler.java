package com.xxexample.selfchapter23.excepetionHandler;

import com.xxexample.selfchapter23.resp.RespVO;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class UserExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespVO HandleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return RespVO.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
