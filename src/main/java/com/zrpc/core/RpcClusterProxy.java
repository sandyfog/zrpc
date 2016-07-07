package com.zrpc.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.zrpc.cluster.RpcRoute;
import com.zrpc.cluster.ServiceDiscovery;
import com.zrpc.cluster.ServiceRegistry;

public class RpcClusterProxy {
	private Class<? extends RpcRoute> routeClazz;
    private IdWorker idWorker = new IdWorker(RpcConfig.NODE_ID);
	
	
	public  RpcClusterProxy(Class<? extends RpcRoute> routeClazz) {
		this.routeClazz = routeClazz;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProxy( Class<T> clazz) {
		String node;
		try {
			node = InetAddress.getLocalHost().getHostAddress();
			ServiceRegistry.getInstace().registerClient(clazz, node);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		RpcRoute rpcRoute = null ;
		try {
			rpcRoute = routeClazz.newInstance();
			ServiceDiscovery.getInstace().discover(clazz, rpcRoute);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		InvocationHandler handler = new RpcInvocationHandler( clazz, rpcRoute);
		T t = (T) Proxy.newProxyInstance(RpcClusterProxy.class.getClassLoader(), new Class[] { clazz }, handler);
		return t;
	}
	
	public class RpcInvocationHandler implements InvocationHandler{

		private Class<?> clazz;
		private RpcRoute rpcRoute;
		public RpcInvocationHandler(Class<?> clazz,RpcRoute rpcRoute){
			this.clazz = clazz;
			this.rpcRoute = rpcRoute;
		}
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			RpcRequest requst = new RpcRequest();
			requst.setRequestId(idWorker.nextId());
			requst.setClassName(clazz.getName());
			requst.setMethodName(method.getName());
			requst.setParameterTypes(method.getParameterTypes());
			requst.setParameters(args);
			RpcResponse response = rpcRoute.select().invoke(requst);
			if(response==null ) return null;
			return response.getResult();
		}}
}
