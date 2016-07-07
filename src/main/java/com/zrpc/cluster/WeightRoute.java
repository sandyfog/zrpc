package com.zrpc.cluster;

public class WeightRoute extends RpcRoute {

	@Override
	public RouteClient select() {

		int total = 0;
		RouteClient best = null;
		for (RouteClient clinet : clients) {

			// 当前服务器已宕机，排除
			if (clinet.isDown()) {
				continue;
			}

			clinet.currentWeight += clinet.weight;
			total += clinet.weight;

			if (best == null || clinet.currentWeight > best.currentWeight) {
				best = clinet;
			}

		}
		if (best == null) {
			return null;
		}
		best.currentWeight -= total;
		return best;
	}

}
