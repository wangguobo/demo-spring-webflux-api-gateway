1.api gateway目前没有专门的需求规范文档，相关接口规范见
http://gitlab.gmit.globalmarket.com:10080/dropship/docs/blob/master/2017-10-30-鹰熊会系统对接/第三方用户注册服务接口.md

2.网关的作用：作为反向代理，对第三方系统和我们自己的app应用提供统一的服务访问入口，接受这些系统和应用的http服务请求，进行鉴权和授权，鉴权通过后讲请求转发给后端的服务。

3.涉及到的技术：springboot2+spring5 webflux包括webclient,提供异步网络io。