package com.zrpc.example.cluster;

import com.zrpc.cluster.Cluster;
import com.zrpc.cluster.RoundRobinRoute;
import com.zrpc.example.HelloService;

public class Clinet {

	public static void main(String[] args) throws Exception {
		//负载均衡设置 
//		Cluster cluster = new Cluster(WeightRoute.class);
//		Cluster cluster = new Cluster(RandomRoute.class);
		Cluster cluster = new Cluster(RoundRobinRoute.class);
		HelloService service = cluster.refer(HelloService.class);
		
		for (int i = 0; i < 10; i++) {			
			System.out.println(service.hello("test rpc"));
		}
		
		
	}

}
