package com.zrpc.core;

import java.util.concurrent.Future;



public final class RpcContext {
	
	private static final ThreadLocal<RpcContext> THREAD_LOCAL = new ThreadLocal<RpcContext>() {
		@Override
		protected RpcContext initialValue() {
			return new RpcContext();
		}
	};
	
	
	private int   rpcTimeoutInMillis =RpcConfig.RPC_TIMEOUT;
	private boolean  oneway ;
	private boolean   async ;
	private RpcFuture<?> future ;
	private Callback callback;
	
	public int getRpcTimeoutInMillis() {
		return rpcTimeoutInMillis;
	}
	
	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void setRpcTimeoutInMillis(int rpcTimeoutInMillis) {
		this.rpcTimeoutInMillis = rpcTimeoutInMillis;
	}

	public boolean isOneway() {
		return oneway;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}
	
	public boolean isAsync() {
		return async;
	}
	
	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setFuture(RpcFuture<?> future) {
		this.future = future;
		if(callback!=null)
			this.future.setCallback(callback);
	}

	
	public static RpcContext getContext() {
	    return THREAD_LOCAL.get();
	}
	
	
	public static void removeContext() {
	    THREAD_LOCAL.remove();
	}
	
	@SuppressWarnings("unchecked")
	public<T>  Future<T> getFuture() {
        return (Future<T>) future;
    }

}
