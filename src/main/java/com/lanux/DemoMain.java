package com.lanux;

import com.lanux.web.DemoVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;

public class DemoMain {
    public static void main(String[] args) {
//        VertxOptions对象有很多配置，包括集群、高可用、池大小等。
//        一个单独的 Event Loop 可以非常迅速地处理数千个 HTTP 请求。
        VertxOptions vertxOptions = new VertxOptions()
        .setEventLoopPoolSize(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE)
        .setWorkerPoolSize(VertxOptions.DEFAULT_WORKER_POOL_SIZE)
        .setInternalBlockingPoolSize(VertxOptions.DEFAULT_INTERNAL_BLOCKING_POOL_SIZE)
        .setBlockedThreadCheckInterval(VertxOptions.DEFAULT_BLOCKED_THREAD_CHECK_INTERVAL)
        .setEventBusOptions(new EventBusOptions()
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("wibble"))
                .setTrustStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("wibble"))
                .setClientAuth(ClientAuth.REQUIRED)
                .setClusterPublicHost("127.0.0.1")
                .setClusterPublicPort(8009)
        );
        Vertx vertx = Vertx.vertx(vertxOptions);

        JsonObject config = new JsonObject().put("name", "tim").put("directory", "/blah");

        DeploymentOptions options = new DeploymentOptions()
                                    .setConfig(config)
                                    .setWorker(false)
                                    .setInstances(16);
        vertx.deployVerticle(DemoVerticle.class.getName(), options, res -> {
            if (res.succeeded()) {
                System.out.println("Deployment id is: " + res.result());
            } else {
                System.out.println("Deployment failed!");
                res.cause().printStackTrace();
            }
        });
    }
}
