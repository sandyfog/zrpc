package com.zrpc.cluster;

import com.zrpc.core.RpcClusterProxy;

public class Cluster {
	private RpcClusterProxy proxy;
	
	public Cluster(Class<? extends RpcRoute> routeClazz){
		this.proxy = new RpcClusterProxy(routeClazz);
	}
	
	public <T> T refer(Class<T> clazz){
		return proxy.getProxy(clazz);
		
	}
}
