package com.zrpc.core;

import java.io.IOException;

public interface RpcAcceptor {
	public void setHost(String host);
	public void setport(int port);
	public void start() throws IOException;
	public void stop() throws IOException;
	public void setProcessor(RpcProcessor processor);
}
