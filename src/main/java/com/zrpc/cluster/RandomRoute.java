package com.zrpc.cluster;

public class RandomRoute extends RpcRoute {

	@Override
	public RouteClient select() {

		int k = (int) (Math.random() * clients.size());
		for (int i = 0; i < clients.size(); i++) {
			RouteClient best = clients.get((i + k) % clients.size());
            if (!best.isDown()) {
                return best;
            }
        }
        return null;
	}

}
