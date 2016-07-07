# ZRPC
基于netty实现的RPC框架

# 点对点调用
## 服务端
```java
  RpcServer server = new RpcServer("127.0.0.1",1234);
  HelloServiceImpl impl = new HelloServiceImpl();
  server.export(HelloService.class, impl);
```
## 客户端
```java
  RpcClient client = new RpcClient("127.0.0.1",1234);
  HelloService service = client.refer(HelloService.class);
  //同步调用
  System.out.println(service.hello("test rpc"));
```
## 异步调用  
默认的远程调用都是同步的，发起异步调用需要设置`RpcContext.setAsync(true) `,异步调用有两种方式：`Future`方式、`callback`方式，可以单独使用也可以混合使用

* `Future`方式

```java
  RpcClient client = new RpcClient("127.0.0.1",1234);
  HelloService service = client.refer(HelloService.class);
  RpcContext ctx = RpcContext.getContext();
  ctx.setAsync(true);
  String obj=service.hello("test rpc");
  System.out.println(obj==null);
  Future<String> f =ctx.getFuture();
  System.out.println(f.get());
```

* `callback`方式

```java
  RpcClient client = new RpcClient("127.0.0.1",1234);
  HelloService service = client.refer(HelloService.class);
  RpcContext ctx = RpcContext.getContext();
  ctx.setAsync(true);
  ctx.setCallback(new Callback() {
			
		@Override
		public void onSuccess(Object result) {
			System.out.println("success  "+ result);
		}
		@Override
		public void onError(Throwable thr) {
		       System.out.println("error");
		       thr.printStackTrace();
		}
		});
  String obj=service.hello("test rpc");
  System.out.println(obj==null);
```
## 单向调用
单向调用是一种特殊类型的异步调用，意味着客户端对本次调用不期待服务端的响应结果。实际上服务端对于单向调用请求也不会作出响应。对于特定场景单向调用性能更好，但并不那么可靠。
```java
//单向调用
  RpcContext ctx = RpcContext.getContext();
  ctx.setOneway(true);
  System.out.println(service.hello("test rpc")==null);
```

# 分布式rpc调用
## 服务端
```java
         RpcServer server = new RpcServer("127.0.0.1",1234,60);//60 weight WeightRoute时起作用
	server.setRegistry(true);          //设置向注册中心注册
	HelloServiceImpl impl = new HelloServiceImpl();
	server.export(HelloService.class, impl);
```
## 客服端
```java
	//负载均衡设置
//	Cluster cluster = new Cluster(WeightRoute.class);
//	Cluster cluster = new Cluster(RandomRoute.class);
	Cluster cluster = new Cluster(RoundRobinRoute.class);
	HelloService service = cluster.refer(HelloService.class);
		
	for (int i = 0; i < 10; i++) {			
		System.out.println(service.hello("test rpc"));
	}
```
