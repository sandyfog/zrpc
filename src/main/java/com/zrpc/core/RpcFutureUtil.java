package com.zrpc.core;

import java.util.HashMap;
import java.util.Map;


public class RpcFutureUtil {
	private Map<Long, RpcFuture<?>>  futures = new HashMap<>();
	
	public void setRpcFuture(long mid, RpcFuture<?> future) {
		futures.put(mid, future);
	}
	
	public void notifyRpcMessage(RpcResponse msg) {
		RpcFuture<?> future = futures.remove(msg.getRequestId());
		if (future == null) return;
		future.setResponse(msg);
		//执行回调
		Callback callback = future.getCallback();
		if(callback!=null){
			if(msg.getError()!=null) callback.onError(msg.getError());
			else callback.onSuccess(msg.getResult());
		}
	}
	
	public void notifyRpcException(Exception e) {
		for (RpcFuture<?> future : futures.values()) {
			future.setException(e);
		}
	}
}
