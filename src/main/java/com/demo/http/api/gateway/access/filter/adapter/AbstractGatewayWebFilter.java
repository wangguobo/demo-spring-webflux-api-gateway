package com.demo.http.api.gateway.access.filter.adapter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

abstract public class AbstractGatewayWebFilter  implements WebFilter{
	private static final String SKIP_PROCESSING_ATTR = "skip-webflux-exchange";
	
	//不建议覆盖改方法
	@Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if(isSkip(exchange)) {
			return chain.filter(exchange);
		}
		else {
			return doFilter(exchange,chain).switchIfEmpty(Mono.just(false)).
					flatMap(filterResult -> filterResult  ?  chain.filter(exchange):  doDenyResponse(exchange));
		}
    }
	
	/*模板方法，子类实现，在该方法中执行过滤逻辑
	 * 
	 * @param serverWebExchange the current server exchange
	 * @webFilterChain provides a way to delegate to the next filter
	 * @return {@code Mono<Boolean>} 过滤结果：通过还是不通过。TRUE表示过滤通过(pass)，控制流传递到下个filter；FALSE 不通过(deny)，
	 * 控制流不会传递到下个filter，而是调用doDenyResponse返回过滤不通过的响应结果。
	 */
	protected abstract Mono<Boolean> doFilter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain);
	
	/*模板方法，子类实现，在该方法中返回过滤未通过的响应结果
	 * 
	 *@param exchange the current server exchange
	 *@return {@code Mono<Void>} 指示响应的处理已完成
	 */
	protected abstract Mono<Void> doDenyResponse(ServerWebExchange exchange);
	
	//指示后续过滤器跳过对该请求的处理，不进入过滤器的doFilter方法
	protected void skipProcess(ServerWebExchange serverWebExchange) {
		serverWebExchange.getAttributes().put(SKIP_PROCESSING_ATTR, true);
	}
	
	//是否跳过对该请求的处理
	private boolean isSkip(ServerWebExchange serverWebExchange) {
		return Boolean.TRUE == (Boolean)serverWebExchange.getAttribute(SKIP_PROCESSING_ATTR);
	}
}
