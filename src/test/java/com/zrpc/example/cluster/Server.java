package com.zrpc.example.cluster;
import java.io.IOException;

import com.zrpc.core.RpcServer;
import com.zrpc.example.HelloService;
import com.zrpc.example.HelloServiceImpl;

public class Server {

	public static void main(String[] args) throws IOException {
		RpcServer server = new RpcServer("127.0.0.1",1234,60);
		server.setRegistry(true);
		HelloServiceImpl impl = new HelloServiceImpl();
		server.export(HelloService.class, impl);
		System.out.println("#################");
	}

}
