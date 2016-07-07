package com.zrpc.cluster;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinRoute extends RpcRoute {
	 private AtomicInteger idx = new AtomicInteger(0);
	@Override
	public RouteClient select() {
		 int k = idx.incrementAndGet();
		 for (int i = 0; i < clients.size(); i++) {
				RouteClient best = clients.get((i + k) % clients.size());
	            if (!best.isDown()) {
	                return best;
	            }
	        }
	        return null;
	}

}
