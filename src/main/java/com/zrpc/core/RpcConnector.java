package com.zrpc.core;

import java.io.IOException;

public interface RpcConnector {
	public RpcResponse invoke(RpcRequest requst) throws IOException;
	public void setHost(String host);
	public void setport(int port);
	public void start() throws IOException ;
	public void stop() throws IOException ;
}
