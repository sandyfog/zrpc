package com.zrpc.core;

import java.util.concurrent.ConcurrentHashMap;

public class Exporter {

	protected ConcurrentHashMap<String, Object> serviceEngine = new ConcurrentHashMap<>();


	public void export(Class<?> clazz, Object obj, String version) {

		try {
			obj.getClass().asSubclass(clazz);
		} catch (ClassCastException e) {
			throw new RpcException(obj.getClass().getName() + " can't cast " + clazz.getName());
		}
		if (version == null) {
			version = RpcConfig.DEFAULT_VERSION;
		}
		String exekey = this.genExeKey(clazz.getName(), version);
		Object service = serviceEngine.get(exekey);
		if (service != null && service != obj) {
			throw new RpcException("can't register service " + clazz.getName() + " again");
		}
		if (obj == service || obj == null) {
			return;
		}
		serviceEngine.put(exekey, obj);
	}

	private String genExeKey(String service, String version) {
		if (version != null) {
			return service + "_" + version;
		}
		return service;
	}

	
	public Object findService(String clazzName, String version) {

		if (version == null) {
			version = RpcConfig.DEFAULT_VERSION;
		}
		String exekey = this.genExeKey(clazzName, version);
		Object service = serviceEngine.get(exekey);
		return service;

	}
}
