package com.zrpc.core;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.zrpc.transport.NettyRpcAcceptor;


public class RpcServer {
	protected ConcurrentHashMap<String, Object> serviceEngine = new ConcurrentHashMap<>();
	private RpcProcessor processor;
	private RpcAcceptor acceptor;
	private Exporter exporter;
	public RpcServer(){}
	public RpcServer(String host,int port){
		exporter = new Exporter();
		acceptor = new NettyRpcAcceptor();
		acceptor.setHost(host);
		acceptor.setport(port);		
		processor = new RpcProcessor(exporter);
		acceptor.setProcessor(processor);
		
		try {
			acceptor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void export(Class<?> clazz, Object obj) {
		export(clazz, obj, null);
	}
	public void export(Class<?> clazz, Object obj, String version) {
		exporter.export(clazz, obj, version);
	}
	
}
