package com.lanux.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DemoVerticle extends AbstractVerticle {



    /**
     * Called when verticle is deployed <br/>
     * <p>
     * 您可以实现 异步版本 的 start 方法来做这个事。这个版本的方法会以一个 Future 作参数被调用。方法执行完时，Verticle 实例并没有部署好（状态不是 deployed）。<br/>
     * 稍后，您完成了所有您需要做的事（如：启动其他Verticle），您就可以调用 Future 的 complete（或 fail ）方法来标记启动完成或失败了。<br/>
     *
     * @param startFuture
     * @throws Exception
     */
    @Override
    public void start(io.vertx.core.Future<Void> startFuture) throws Exception {
        // 现在部署其他的一些verticle
//        vertx.deployVerticle("com.foo.OtherVerticle", res -> {
//            if (res.succeeded()) {
//                startFuture.complete();
//            } else {
//                startFuture.fail(res.cause());
//            }
//        });

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            // headers 键值不区分大小写
            MultiMap headers = request.headers();
            HttpMethod method = request.method();
            String uri = request.uri();
            String query = request.query();
            MultiMap params = request.params();
            String host = request.host();
            SocketAddress socketAddress = request.remoteAddress();
            HttpVersion version = request.version();
            request.endHandler(e->{
                System.out.println("e = " + e);
            });// 当整个请求（包括任何正文）已经被完全读取时，请求中的 endHandler 方法会被调用
            int i = RandomUtils.nextInt();
            System.out.println("User agent is "+ i +" " + headers.get("user-agent"));

            request.response().end("Hello world = "+ i);
        })// 当请求的头信息被完全读取时会调用该请求处理器。如果请求包含请求体，那么该请求体将在请求处理器被调用后的某个时间到达服务器。
                .listen(8889, "0.0.0.0", res -> {
                    if (res.succeeded()) {
                        System.out.println("Server is now listening!");
                    } else {
                        System.out.println("Failed to bind!");
                    }
                });// 默认主机名是0.0.0.0，它表示：监听所有可用地址；默认端口号是80。


        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route().handler(routingContext -> {

            // 所有的请求都会调用这个处理器处理
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            log.info("hello");
            // 写入响应并结束处理
            response.end("Hello World from Vert.x-Web!");
        });

        httpServer.requestHandler(router::accept).listen(8899);
    }

//    Called when verticle is deployed
//    public void start() {
//    }

    // Optional - called when verticle is undeployed
    public void stop() {
    }
}
