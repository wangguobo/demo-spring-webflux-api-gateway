package com.demo.http.api.gateway.access.controller;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.demo.http.api.gateway.constant.Constant;

import reactor.core.publisher.Mono;

/**
* api反向代理控制器，转发调用后端服务
* 
* @author wang guobo 王国波
*/
@RestController
public class ApiProxyController {
	static final String APPKEY_HTTP_HEAD = "appKey";
	WebClient webClient = WebClient.create();
    //后端服务url
	@Value("${backend.service.url.prefix}")
	String backendServiceUrlPrefix;
	//后端服务超时
	@Value("${backend.service.timeout.inmillis}")
	long backendServiceTimeoutInMillis;
	
    @RequestMapping("/**")
    /**
     * 
     * @param exchange 
     * @return
     */
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
		                  httpHeaders.remove(HttpHeaders.HOST);
		                });
		
		RequestHeadersSpec<?> headersSpec;
	    if (requiresBody(httpMethod)) {
	          headersSpec = reqBodySpec.body(BodyInserters.fromDataBuffers(frontEndReq.getBody()));
	    } else {
	          headersSpec = reqBodySpec;
	    	}
	    
		//调用后端服务
		return headersSpec.exchange().timeout(Duration.ofMillis(backendServiceTimeoutInMillis)).
				onErrorResume(ex -> 
			    { 
				    //调用后端服务异常(超时、网络问题)时，转发到后端异常-后控制器
			    	    WebFilterChain webFilterChain = (WebFilterChain)exchange.getAttributes().get(Constant.WEB_FILTER_ATTR_NAME);
			        ServerHttpRequest exceptionReq = frontEndReq.mutate().path(Constant.BACKEND_EXCEPTION_PATH).build();
	                ServerWebExchange forwardExchange = exchange.mutate().request(exceptionReq).build();
	                forwardExchange.getAttributes().put(Constant.BACKEND_EXCEPTION_ATTR_NAME,ex);
				    return webFilterChain.filter(forwardExchange).then(Mono.empty());
			    }).flatMap(backendResponse -> 
			    {
			    	    //将后端服务的响应回写到前端resp
			    	    frontEndResp.setStatusCode(backendResponse.statusCode());
			    	    backendResponse.headers().asHttpHeaders().entrySet().stream().
			    	        forEach(entry -> frontEndResp.getHeaders().addAll(entry.getKey(), entry.getValue()));
			    	    return frontEndResp.writeWith(backendResponse.bodyToFlux(DataBuffer.class));
			    	}
			    	).then(Mono.empty());
	}
    
    private boolean requiresBody(HttpMethod method)
    {
    	    return HttpMethod.POST == method || HttpMethod.PUT == method || 
    	    		   HttpMethod.PATCH == method || HttpMethod.TRACE == method ;
    }

}
