package com.zrpc.core;

public interface Callback {
	
	public void onSuccess (Object result);

	public void onError(Throwable thr);
	
}
