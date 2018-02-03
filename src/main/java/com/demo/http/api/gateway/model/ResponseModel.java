package com.demo.http.api.gateway.model;

import com.demo.http.api.gateway.constant.ErrorEnum;
/**
* response model
* 
* @author wang guobo 王国波
*/
public class ResponseModel {
    int code;
    String message;
    boolean success = false;

    public ResponseModel(ErrorEnum error)
    {
        code = error.getCode();
        message = error.getMessage();
    }
    
    public ResponseModel(ErrorEnum error,Throwable ex)
    {
        code = error.getCode();
        message = error.getMessage().concat(",").concat(ex.getMessage());
    }
    
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
