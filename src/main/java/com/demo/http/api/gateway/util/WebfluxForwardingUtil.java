package com.demo.http.api.gateway.util;

import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.demo.http.api.gateway.constant.Constant;

import reactor.core.publisher.Mono;
/**
* webflux forward util
* 
* @author wang guobo 王国波
*/
public class WebfluxForwardingUtil {
	/**
	 * 
	 * @param forwardToPath: forward target path that begin with /.
	 * @param exchange: the current source server exchange
	 * @param forwardAttrs : the attributes that added to forward Exchange.
	 * @return Mono<Void> to signal forwarding request completed.
	 */
	public static Mono<Void> forward(String forwardToPath,ServerWebExchange exchange,Map<String,Object> forwardAttrs){
	    WebFilterChain webFilterChain = (WebFilterChain)exchange.getAttributes().get(Constant.WEB_FILTER_ATTR_NAME);
	    ServerHttpRequest forwardReq = exchange.getRequest().mutate().path(forwardToPath).build();
	    ServerWebExchange forwardExchange = exchange.mutate().request(forwardReq).build();
        if(null != forwardAttrs && !forwardAttrs.isEmpty()) {
        		forwardExchange.getAttributes().putAll(forwardAttrs);
        }
        return webFilterChain.filter(forwardExchange);
	}
}
