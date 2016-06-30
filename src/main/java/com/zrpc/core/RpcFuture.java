package com.zrpc.core;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class RpcFuture<V> implements Future<V> {
	
	
	private volatile RpcResponse response ;
	private volatile Exception  exception;
	private volatile boolean    done     ;
	private volatile int        waiters  ;
	private volatile Callback callback;
	 
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		long timeoutMillis = unit.toMillis(timeout);
		long endTime       = System.currentTimeMillis() + timeoutMillis;
		synchronized (this) {
			if (done)               return done;
			if (timeoutMillis <= 0) return done;
			waiters++;
			try {
				while (!done) {
					wait(timeoutMillis);
					if (endTime < System.currentTimeMillis() && !done) {
						exception = new TimeoutException("time out");
						break;
					}
				}
			} finally {
				waiters--;
			}
		}
		return done;
	}

	public Exception getException() {
		return exception;
	}

	public RpcResponse getResponse() throws IOException, TimeoutException {
		Exception e = getException();
		if (e != null) {
			if (e instanceof IOException     ) throw (IOException)      e;
			if (e instanceof TimeoutException) throw (TimeoutException) e;
			throw new RpcException("unkonw error", e);
		}
		return response;
	}

	public void setException(Exception exception) {
		synchronized (this) {
			if (done) return;
			this.exception = exception;
			done = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}

	public void setResponse(RpcResponse response) {
		synchronized (this) {
			if (done) return;
			this.response = response;
			done = true;
			if (waiters > 0) {
                notifyAll();
            }
		}
	}
	
	@Override
	public V get() throws InterruptedException, ExecutionException {
		try { return get(Long.MAX_VALUE, TimeUnit.DAYS); } catch (TimeoutException e) { throw new InterruptedException(e.getMessage()); }
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		await(timeout, unit);
		try {
			RpcResponse rsp = getResponse();
			return (V) rsp.getResult();
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	public Callback getCallback() {
		return callback;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

}
