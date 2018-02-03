package com.demo.http.api.gateway.constant;
/**
* 内部constant
* 
* @author wang guobo 王国波
*/
public class Constant {
	//webflux controller mapping path
	public static final String AUTH_FAILED_PATH = "/auth/failed";
	public static final String BACKEND_EXCEPTION_PATH = "/backend/exception";
	
	public static final String WEB_FILTER_ATTR_NAME = "filterchain";
	//认证失败属性名
	public static final String AUTH_ERROR_ATTR_NAME = "auth-error";
	//后端服务异常属性名
	public static final String BACKEND_EXCEPTION_ATTR_NAME = "backend-exception";
	
}
