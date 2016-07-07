package com.zrpc.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zrpc.core.RpcConnector;
import com.zrpc.transport.NettyRpcConnector;

public abstract class RpcRoute {
	private static Map<String, RpcConnector> map = new ConcurrentHashMap<>();
	protected List<RouteClient> clients;
	private NotifyListener listener = new DefaultNotify();
	public void onRefresh(List<RouteClient> clients) {

		this.clients = clients;
	}

	abstract public RouteClient select();
	
	public NotifyListener getListener() {
		return listener;
	}

	public class DefaultNotify implements NotifyListener {

		@Override
		public void notify(List<String> servers, RpcRoute route) {
			// 127.0.0.1:1234:30 host:prot:weight
			List<RouteClient> list = new ArrayList<>();
			for (String server : servers) {
				RpcConnector c = map.get(server);
				String[] tem = server.split(":");
				if (c == null) {
					c = new NettyRpcConnector();
					c.setHost(tem[0]);
					c.setport(Integer.parseInt(tem[1]));
					try {
						c.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				list.add(new RouteClient(c, Integer.parseInt(tem[2])));
			}

			route.onRefresh(list);
		}
	}

	
}
