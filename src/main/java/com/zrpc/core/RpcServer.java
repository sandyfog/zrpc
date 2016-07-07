package com.zrpc.core;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.zrpc.cluster.ServiceRegistry;
import com.zrpc.transport.NettyRpcAcceptor;


public class RpcServer {
	protected ConcurrentHashMap<String, Object> serviceEngine = new ConcurrentHashMap<>();
	private RpcProcessor processor;
	private RpcAcceptor acceptor;
	private Exporter exporter;
	private String host;
	private int port;
	private int weight;
	private  boolean registry = false;
	
	public RpcServer(String host,int port){
	   this(host, port, 100);
	}
	public RpcServer(String host,int port, int weight){
		this.host = host;
		this.port = port;
		this.weight = weight;
		
		exporter = Exporter.getInstace();
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
	
	public void setRegistry(boolean registry) {
		this.registry = registry;
	}
	public void export(Class<?> clazz, Object obj) {
		export(clazz, obj, null);
	}
	public void export(Class<?> clazz, Object obj, String version) {
		exporter.export(clazz, obj, version);
		if(registry){
			ServiceRegistry.getInstace().registerServer(clazz, host+":"+port, ""+weight);
		}
	}
	
}
