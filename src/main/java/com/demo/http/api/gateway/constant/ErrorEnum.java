package com.demo.http.api.gateway.constant;
/**
* 错误码
* 
* @author wang guobo 王国波
*/
public enum ErrorEnum {
    NO_APPKEY(350,"缺少appKey头部"),NO_SIGNATURE(351,"缺少signature"),NO_TIMESTAMP(352,"缺少timestamp"),
    INVALID_TIMESTAMP(353,"无效时间戳"),INVALID_SIGNATURE(354,"无效签名"),
    NO_AUTH(355,"鉴权失败(无权限)"),NO_SERVICE(356,"鉴权失败(无此接口)"),NOAVAILABLE_SERVICE(357,"后端无存活服务"),
    
    BACKEND_CONNREFUSED(370,"后端服务拒绝连接"),BACKEND_COMEXCEPTION(371,"网关与后端通讯异常"),
    BACKEND_TIMEOUT(372,"后端服务超时"),
    BACKEND_4XX_5XX(373,"后端http异常(4XX or 5xx status code)"),OTHER(374,"系统其他错误");
    
    private int  code;
    private String message;

    ErrorEnum(int code,String message)
    {
        this.code= code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
