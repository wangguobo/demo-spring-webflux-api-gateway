package com.demo.http.api.gateway.access.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
* 请求限流过滤器
* 
* @author wang guobo 王国波
*/
@Order(2)
@Component
public class RateLimitFilter  implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		//doRateLimit(exchange, chain);
		return chain.filter(exchange);
	}
}
