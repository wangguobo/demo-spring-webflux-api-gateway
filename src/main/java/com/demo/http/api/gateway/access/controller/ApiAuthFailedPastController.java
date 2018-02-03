package com.demo.http.api.gateway.access.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.demo.http.api.gateway.constant.Constant;
import com.demo.http.api.gateway.constant.ErrorEnum;
import com.demo.http.api.gateway.model.ResponseModel;
import reactor.core.publisher.Mono;


/**
* 认证失败后控制器
* 
* @author wang guobo 王国波
*/
@RestController
public class ApiAuthFailedPastController {

    @RequestMapping(Constant.AUTH_FAILED_PATH)
    public Mono<ResponseModel> processIllegalRequest(ServerWebExchange exchange)
    {
        ErrorEnum error = (ErrorEnum)exchange.getAttributes().get(Constant.AUTH_ERROR_ATTR_NAME);
        //根据error的枚举值，设置不同的http status Code，如400 Bad Request，401 Unauthorized
        //这里只是演示，统一设置为401 Unauthorized
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return Mono.just(new ResponseModel(error));
    }
    
    /*
    @RequestMapping(Constant.AUTH_FAILED_PATH)
    public Mono<Void> processIllegalRequest(ServerWebExchange exchange)
    {
        ErrorEnum error = (ErrorEnum)exchange.getAttributes().get(Constant.AUTH_ERROR_ATTR_NAME);
        //根据error的枚举值，设置不同的http status Code，如400 Bad Request，401 Unauthorized
        //这里只是演示，统一设置为401 Unauthorized
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return Mono.empty();
    }
    */
}
