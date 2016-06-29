package com.zrpc.example;

import java.util.concurrent.Future;

import com.zrpc.core.Callback;
import com.zrpc.core.RpcClient;
import com.zrpc.core.RpcContext;

public class Clinet {

	public static void main(String[] args) throws Exception {
		RpcClient client = new RpcClient("127.0.0.1",1234);
		HelloService service = client.refer(HelloService.class);
		
		//同步调用
		System.out.println(service.hello("test rp0000c"));
		
		//异步调用1
		RpcContext ctx = RpcContext.getContext();
		ctx.setAsync(true);
		String obj=service.hello("test rp0000c");
		System.out.println(obj==null);
		Future<String> f =ctx.getFuture();
		System.out.println(f.get());
		
		// 异步调用2
		ctx = RpcContext.getContext();
		ctx.setAsync(true);
		
		ctx.setCallback(new Callback() {
			
			@Override
			public void onSuccess(Object result) {
				System.out.println("success  "+ result);
			}
			
			@Override
			public void onError(Throwable thr) {
				System.out.println("thr");
				thr.printStackTrace();
			}
		});
		
		String result = service.hello("test rp0000c");
		System.out.println(result == null);
		

//		//单向调用
		/**
		 * 单向调用是一种特殊类型的异步调用，意味着客户端对本次调用不期待服务端的响应结果。
		 * 实际上服务端对于单向调用请求也不会作出响应。对于特定场景单向调用性能更好，但并不那么可靠.
		 */
//		RpcContext ctx = RpcContext.getContext();
//		ctx.setOneway(true);
//		System.out.println(service.hello("test rp0000c"));
	}

}
