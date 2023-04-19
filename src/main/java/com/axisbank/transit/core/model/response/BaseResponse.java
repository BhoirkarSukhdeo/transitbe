package com.axisbank.transit.core.model.response;

public class BaseResponse<T>{
    private Integer code;
    private String message;
    private T data;

    public BaseResponse() {
    }
    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public java.lang.String getMessage() {
        return message;
    }

    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "Code=" + code +
                ", message='" + message + '\'' +
                ", Data=" + data +
                '}';
    }

}
