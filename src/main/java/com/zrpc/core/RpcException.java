package com.zrpc.core;

public class RpcException extends RuntimeException{
	
	private static final long serialVersionUID = 6238589897120159526L;

	public RpcException(){
		super();
	}
	
	public RpcException(String message){
		super(message);
	}
	public RpcException(String message,Throwable thr){
		super(message,thr);
	}
	public RpcException(Throwable thr){
		super(thr);
	}

}
