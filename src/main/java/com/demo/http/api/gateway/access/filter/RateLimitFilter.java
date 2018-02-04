package com.demo.http.api.gateway.access.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.demo.http.api.gateway.access.filter.base.AbstractGatewayWebFilter;

import reactor.core.publisher.Mono;

/**
* 请求限流过滤器
* 
* @author wang guobo 王国波
*/
@Order(2)
@Component
public class RateLimitFilter  extends AbstractGatewayWebFilter  {

    @Override
	protected Mono<Boolean> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
		return doRateLimit(exchange);
	}

	@Override
	protected Mono<Void> doDenyResponse(ServerWebExchange exchange) {
		// TODO Auto-generated method stub
		return Mono.empty();
	}
	
	private Mono<Boolean> doRateLimit(ServerWebExchange exchange){
		//不限流，都让通过
		return Mono.just(true);
	}
	
}
