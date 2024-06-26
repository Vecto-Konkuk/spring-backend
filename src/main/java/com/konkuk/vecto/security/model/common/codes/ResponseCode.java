package com.konkuk.vecto.security.model.common.codes;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCode<T> {

    private int status;

    private String code;

    private String message;

    private T result;

    public ResponseCode(int status, String code, String message) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public ResponseCode(SuccessCode successCode){
        this.status = successCode.getStatus();
        this.code = successCode.getCode();
        this.message = successCode.getMessage();
    }

    public ResponseCode(ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public void setResult(T result){
        this.result = result;
    }

}
