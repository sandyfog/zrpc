package com.zrpc.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class RpcProcessor {
	private ExecutorService executorService;
	private Exporter exporter;
	public RpcProcessor (Exporter exporter){
		executorService = Executors.newFixedThreadPool(RpcConfig.EXECUTOR_THREAD_COUNT);
		this.exporter = exporter;
	}
	

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	
	public void submit(Runnable job){
		executorService.submit(job);
	}
	
	public Object findService(String clazzName) {
		return findService(clazzName, null);
	}

	public Object findService(String clazzName, String version) {
		return exporter.findService(clazzName, version);
	}
	
	
}
