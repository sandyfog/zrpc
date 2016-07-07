package com.zrpc.core;

import java.io.IOException;

import com.zrpc.transport.NettyRpcConnector;

public class RpcClient {
	
	private RpcProxy proxy ;
	private RpcConnector connector ;
	public RpcClient(String host,int port){
		proxy = new RpcProxy();
		connector = new NettyRpcConnector();
		connector.setHost(host);
		connector.setport(port);
		proxy.setConnector(connector);
		try {
			connector.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public <T> T refer(Class<T> clazz){
		return proxy.getProxy(clazz);
		
	}
	
	public void stop(){
		try {
			connector.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
