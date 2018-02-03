package com.demo.http.api.gateway.access.controller;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.demo.http.api.gateway.constant.Constant;
import com.demo.http.api.gateway.constant.ErrorEnum;
import com.demo.http.api.gateway.model.ResponseModel;

import reactor.core.publisher.Mono;

/**
* 处理后端服务异常(网关调用后端服务，后端服务发生异常)的后控制器，产生fallback响应
* 
* @author wang guobo 王国波
*/
@RestController
public class ApiBackendExceptionPastController {

    @RequestMapping(Constant.BACKEND_EXCEPTION_PATH)
    public Mono<ResponseModel> processExceptionRequest(ServerWebExchange exchange)
    {
    		Throwable ex = (Throwable)exchange.getAttributes().get(Constant.BACKEND_EXCEPTION_ATTR_NAME);
    		if(ex instanceof ConnectException) {
		    return Mono.just(new ResponseModel(ErrorEnum.BACKEND_CONNREFUSED,ex));
		}
		else if(ex instanceof TimeoutException) {
			return Mono.just(new ResponseModel(ErrorEnum.BACKEND_TIMEOUT,ex));
		}
		else if(ex instanceof SocketException) {
			return Mono.just(new ResponseModel(ErrorEnum.BACKEND_COMEXCEPTION,ex));
		}
		else if(ex instanceof WebClientResponseException) {
			//http status code: 4xx or 5xx
			return Mono.just(new ResponseModel(ErrorEnum.BACKEND_4XX_5XX,ex));
		}
		else {
			return Mono.just(new ResponseModel(ErrorEnum.OTHER,ex));	
		}
    }
}
