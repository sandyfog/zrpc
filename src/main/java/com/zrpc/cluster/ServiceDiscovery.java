package com.zrpc.cluster;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zrpc.core.RpcConfig;

public class ServiceDiscovery {

	 private static final Logger LOG = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private  ZooKeeper zk ;

    public ServiceDiscovery() {

        zk = connectServer();
        
    }

    public void discover(Class<?> clazz,RpcRoute route) {
    	if (zk != null) {
            watchNode(zk,clazz,route);
        }
    
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(RpcConfig.ZK_ADDRESS, RpcConfig.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
        	LOG.error("", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk,final Class<?> clazz,final RpcRoute route) {
    	
        try {
        	String path=RpcConfig.ZK_REGISTRY_PATH+"/"+clazz.getName()+RpcConfig.ZK_SERVER_PATH;
            List<String> nodeList = zk.getChildren(path, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    	
                    	watchNode(zk,clazz,route);
                    }
                }
            });
            System.out.println("############### "+nodeList);
            route.getListener().notify(nodeList, route);
        } catch (KeeperException | InterruptedException e) {
        	LOG.error("", e);
        }
    }
    
    public static ServiceDiscovery getInstace(){
		return SingletonHolder.INSTNACE;
	}
	
	private static class SingletonHolder  {
		 public static final ServiceDiscovery INSTNACE = new ServiceDiscovery();
	}
	
    public static void main(String[] args) throws Exception {
    	ServiceDiscovery discovery =  ServiceDiscovery.getInstace();
    	WeightRoute route = new WeightRoute();
    	discovery.discover(ServiceRegistry.class,route);
    	for (int i = 0; i < 10; i++) {
    		System.out.println(route.select());
		}
    	
    	Thread.sleep(100000);
    }
}