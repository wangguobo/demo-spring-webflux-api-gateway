package com.demo.http.api.gateway.access.filter;

import static com.demo.http.api.gateway.constant.ErrorEnum.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.demo.http.api.gateway.access.filter.base.AbstractGatewayWebFilter;
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
public  class RequestAuthFilter extends AbstractGatewayWebFilter {
    static final String APPKEY_HTTP_HEAD = "appKey";
    
    @Autowired
    AppInfoProvider appInfoProvider;

    @Override
	protected Mono<Boolean> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
    	    exchange.getAttributes().put(Constant.WEB_FILTER_ATTR_NAME,chain);
    	    
    		return Mono.just(authRequest(exchange));
	}

	@Override
	protected Mono<Void> doDenyResponse(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		//should use jackson ObjectMapper to  serial a ResponseModel instance to string
		String denyHttpBody = "{\"success\":false,\"code\":355,\"message\":\"unauthorized\"}";
		return response.writeWith(Mono.just(response.bufferFactory().wrap(denyHttpBody.getBytes())));
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
        
        return true;
    }
}
