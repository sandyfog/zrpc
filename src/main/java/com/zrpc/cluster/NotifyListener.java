package com.zrpc.cluster;

import java.util.List;

public interface NotifyListener {
	
	public void notify(List<String> servers,RpcRoute route);

}
