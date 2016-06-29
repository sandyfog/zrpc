package com.zrpc.example;
import java.io.IOException;

import com.zrpc.core.RpcServer;

public class Server {

	public static void main(String[] args) throws IOException {
		RpcServer server = new RpcServer("127.0.0.1",1234);
		HelloServiceImpl impl = new HelloServiceImpl();
		server.export(HelloService.class, impl);
		System.out.println("#################");
	}

}
