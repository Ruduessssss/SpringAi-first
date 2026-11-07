package com.lycai.common.exception;


import org.springframework.http.ResponseEntity;
import com.lycai.common.dto.ErrorResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    //处理代码中主动抛出的,带有HTTP状态码的异常
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex)
    {
        String message = ex.getReason();
        if(message==null||message.isBlank())
        {
            message="请求处理失败";
        }

        ErrorResponse body = new ErrorResponse(message);
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(body);

    }


    // 处理请求体读取或 JSON 解析失败
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(
            HttpMessageNotReadableException ex) {

        ErrorResponse body =
                new ErrorResponse("请求体为空或无法解析，请检查 JSON 格式和字段类型");

        return ResponseEntity
                .badRequest()
                .body(body);
    }


}
