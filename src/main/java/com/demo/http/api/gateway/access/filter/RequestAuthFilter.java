package com.demo.http.api.gateway.access.filter;

import static com.demo.http.api.gateway.constant.ErrorEnum.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.demo.http.api.gateway.constant.Constant;
import com.demo.http.api.gateway.service.AppInfoProvider;
import com.demo.http.api.gateway.util.WebfluxForwardingUtil;

import reactor.core.publisher.Mono;

/**
* http请求认证授权过滤器
* 
* @author wang guobo 王国波
*/
@Order(1)
@Component
public  class RequestAuthFilter implements WebFilter {
    static final String APPKEY_HTTP_HEAD = "appKey";
    
    @Autowired
    AppInfoProvider appInfoProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    	    exchange.getAttributes().put(Constant.WEB_FILTER_ATTR_NAME,chain);
    	    if(!authRequest(exchange)) {
    		   //forward to 验证失败后controller
    	    	   return WebfluxForwardingUtil.forward(Constant.AUTH_FAILED_PATH, exchange,null);
        }
        else {
        	   //forward to 反向代理reverse proxy controller
           return chain.filter(exchange);
        }
    }
    
    /**
     * 请求认证、授权
     * @param exchange
     * @return  验证失败返回false,成功true
     */
    private boolean authRequest(ServerWebExchange exchange)
    {
        ServerHttpRequest frontReq =  exchange.getRequest();
        
        String appKey = frontReq.getHeaders().getFirst(APPKEY_HTTP_HEAD);
        if (null == appKey) {
            exchange.getAttributes().put(Constant.AUTH_ERROR_ATTR_NAME,NO_APPKEY);
            return false;
        }
        
        /*
        AppInfo appInfo= appInfoProvider.getAppInfoByAppKey(appKey);
        if (null == appInfo) {
          	serverWebExchange.getAttributes().put(Constant.VERVIFY_ERROR_ATTR_NAME,INVALID_APPKEY);
            return false;
        }
        */

        return true;
    }

}
