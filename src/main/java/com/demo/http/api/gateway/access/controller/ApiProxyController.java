package com.demo.http.api.gateway.access.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.server.ServerWebExchange;

import com.demo.http.api.gateway.constant.Constant;
import com.demo.http.api.gateway.util.WebfluxForwardingUtil;

import reactor.core.publisher.Mono;

/**
* api反向代理控制器，转发调用后端服务
* 
* @author wang guobo 王国波
*/
@RestController
public class ApiProxyController {
	WebClient webClient = WebClient.create();
	
    //后端服务url
	@Value("${backend.service.url.prefix}")
	String backendServiceUrlPrefix;
	
	//后端服务超时
	@Value("${backend.service.timeout.inmillis}")
	long backendServiceTimeoutInMillis;
	
	/**
     * 
     * @param exchange 
     * @return
     */
    @RequestMapping("/**")
    public Mono<Void>  proxyRequest(ServerWebExchange exchange)
	{
		ServerHttpRequest frontEndReq = exchange.getRequest();
		ServerHttpResponse frontEndResp = exchange.getResponse();
		String path = frontEndReq.getPath().pathWithinApplication().value();
		HttpMethod httpMethod = frontEndReq.getMethod();
		RequestBodySpec reqBodySpec = webClient.method(httpMethod).
				uri(backendServiceUrlPrefix.concat(path)).
				headers(httpHeaders -> 
				        {
		                  httpHeaders.addAll(frontEndReq.getHeaders());
		                  httpHeaders.remove("HOST");
		                });
		
		RequestHeadersSpec<?> reqHeadersSpec;
	    if (requireHttpBody(httpMethod)) {
	    		  reqHeadersSpec = reqBodySpec.body(BodyInserters.fromDataBuffers(frontEndReq.getBody()));
	    } else {
	    	      reqHeadersSpec = reqBodySpec;
	    	}
	    
		//调用后端服务
		return reqHeadersSpec.exchange().timeout(Duration.ofMillis(backendServiceTimeoutInMillis)).
				onErrorResume(ex -> 
			    { 
				    //调用后端服务异常(超时、网络问题)时，转发到后端异常-后控制器
			    	    //此处也可不用转发，用frontEndResp.writeWith(...)直接将异常响应写回给调用方
			    	    Map<String,Object> forwardAttrs = new HashMap<>();
			    	    forwardAttrs.put(Constant.BACKEND_EXCEPTION_ATTR_NAME,ex);
	                return WebfluxForwardingUtil.forward(Constant.BACKEND_EXCEPTION_PATH,exchange,forwardAttrs)
	                		.then(Mono.empty());
				}).flatMap(backendResponse -> 
			    {
			    	    //将后端服务的响应回写到前端resp
			    	    frontEndResp.setStatusCode(backendResponse.statusCode());
			    	    frontEndResp.getHeaders().putAll(backendResponse.headers().asHttpHeaders());
			    	    return frontEndResp.writeWith(backendResponse.bodyToFlux(DataBuffer.class));
			    	}
			    	);
	}
    
    private boolean requireHttpBody(HttpMethod method)
    {
    	    return HttpMethod.POST == method || HttpMethod.PUT == method || 
    	    		   HttpMethod.PATCH == method || HttpMethod.TRACE == method ;
    }

}
