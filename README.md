# vertx-demo


```java
// VertxOptions对象有很多配置，包括集群、高可用、池大小等。
VertxOptions vertxOptions = new VertxOptions();
vertxOptions.setEventLoopPoolSize(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE);
vertxOptions.setWorkerPoolSize(VertxOptions.DEFAULT_WORKER_POOL_SIZE);
vertxOptions.setInternalBlockingPoolSize(VertxOptions.DEFAULT_INTERNAL_BLOCKING_POOL_SIZE);
Vertx vertx = Vertx.vertx(vertxOptions);
```

Vert.x使用被称为 EventLoop 的线程来调用处理器处理IO请求。一个EventLoop相当于一个线程。
由于没有阻塞，EventLoop可在短时间内分发大量的事件。我们称之为 Reactor 模式。

> 例如，一个单独的 Event Loop 可以非常迅速地处理数千个 HTTP 请求。

在一个标准的Reactor实现中，有一个独立的EventLoop会循环执行，处理所有到达的事件并传递给业务处理线程处理。

Vert.x的工作方式有所不同。每个 Vertx 实例维护的是多个EventLoop线程。
默认情况下，我们会根据机器上可用的核数量来设置EventLoop的数量，您亦可自行设置。

以下是一些事件的例子：
- 触发一个计时器
- Socket 收到了一些数据
- 从磁盘中读取了一些数据
- 发生了一个异常
- HTTP 服务器收到了一个请求

> 注意：即使一个 Vertx 实例维护了多个 Event Loop，任何一个特定的处理器永远不会被并发执行。大部分情况下（除了 Worker Verticle 以外）它们总是在同一个 Event Loop 线程中被调用。


### 不要阻塞EventLoop
这些阻塞做法包括：
- Thead.sleep()
- 等待一个锁
- 等待一个互斥信号或监视器（例如同步的代码块）
- 执行一个长时间数据库操作并等待其结果
- 执行一个复杂的计算，占用了可感知的时长
- 在循环语句中长时间逗留

Vert.x提供一个专有线程监控EventLoop是否被阻塞，若检测到 EventLoop 有一段时间没有响应，将会自动记录这种警告。若您在日志中看到类似警告，那么您需要检查您的代码。

> 如果想更改设置，您可以在创建 Vertx 对象之前在 `VertxOptions` 中完成此操作。<br/>
> setWarningExceptionTime<br/>
> 如果线程阻塞时间超过了这个阀值，那么就会打印警告的堆栈信息，默认为5l 1000 1000000，单位ns，即5秒；<br/>
> setBlockedThreadCheckInterval<br/>
> 阻塞线程检查的时间间隔，默认1000，单位ms，即1秒；<br/>


Verticle 种类

这儿有三种不同类型的 Verticle：

- Stardand Verticle：这是最常用的一类 Verticle —— 它们永远运行在 Event Loop 线程上。稍后的章节我们会讨论更多。
- Worker Verticle：
这类 Verticle 会运行在 Worker Pool 中的线程上。一个实例绝对不会被多个线程同时执行，但它可以在不同时间由不同线程执行。
- Multi-Threaded Worker Verticle：这类 Verticle 也会运行在 Worker Pool 中的线程上。一个实例可以由多个线程同时执行（译者注：因此需要开发者自己确保线程安全）。

当 Vert.x 传递一个事件给处理器或者调用 Verticle 的 start 或 stop 方法时，它会关联一个 Context 对象来执行。
每个 Verticle 在部署的时候都会被分配一个 Context（根据配置不同，可以是Event Loop Context 或者 Worker Context），
之后此 Verticle 上所有的普通代码都会在此 Context 上执行（即对应的 Event Loop 或Worker 线程）。
一个 Context 对应一个 Event Loop 线程（或 Worker 线程），但一个 Event Loop 可能对应多个 Context。

您可能会问自己：如何让多台服务器在同一主机和端口上侦听？尝试部署一个以上的实例时真的不会遇到端口冲突吗？

Vert.x在这里有一点魔法。

当您在与现有服务器相同的主机和端口上部署另一个服务器实例时，实际上它并不会尝试创建在同一主机/端口上侦听的新服务器实例。

相反，它内部仅仅维护一个服务器实例。当传入新的连接时，它以轮询的方式将其分发给任意一个连接处理器处理。

因此，Vert.x TCP 服务端可以水平扩展到多个核，并且每个实例保持单线程环境不变。
