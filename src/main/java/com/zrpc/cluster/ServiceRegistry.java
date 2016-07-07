package com.zrpc.cluster;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zrpc.core.RpcConfig;

public class ServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public void registerClient(Class<?> clazz,String node) {
        if (node != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
            	createClient(zk, clazz,node);
            }
        }
    }
    
    public void registerServer(Class<?> clazz,String node,String data) {
        if (node != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
            	createServer(zk, clazz,node,data);
            }
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

    private void createServer(ZooKeeper zk,Class<?> clazz, String node,String data) {
        try {
        	
        	createNode(zk, RpcConfig.ZK_REGISTRY_PATH, "", CreateMode.PERSISTENT);
            String path = RpcConfig.ZK_REGISTRY_PATH+"/"+clazz.getName();
            createNode(zk, path, "", CreateMode.PERSISTENT);
            path = path +RpcConfig.ZK_SERVER_PATH;
            createNode(zk, path, "", CreateMode.PERSISTENT);
            path = path + "/"+node+":"+data;
            createNode(zk, path, "", CreateMode.EPHEMERAL);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
    
    private void createClient(ZooKeeper zk,Class<?> clazz, String node) {
        try {
        	
        	createNode(zk, RpcConfig.ZK_REGISTRY_PATH, "", CreateMode.PERSISTENT);
            String path = RpcConfig.ZK_REGISTRY_PATH+"/"+clazz.getName();
            createNode(zk, path, "", CreateMode.PERSISTENT);
            path = path +RpcConfig.ZK_CLIENT_PATH;
            createNode(zk, path, "", CreateMode.PERSISTENT);
            path = path + "/"+node;
            createNode(zk, path, "", CreateMode.EPHEMERAL);
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
    
    private void createNode(ZooKeeper zk,String path, String data,CreateMode createMode) throws Exception {
		if (zk.exists(path, false) == null) {
			String p = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
			LOG.info("create zookeeper node ({} => {})", p, data);
		}
    }
    
    
    public static ServiceRegistry getInstace(){
		return SingletonHolder.INSTNACE;
	}
	
	private static class SingletonHolder  {
		 public static final ServiceRegistry INSTNACE = new ServiceRegistry();
	}
	
    public static void main(String[] args) throws Exception {
    	ServiceRegistry serviceRegistry = ServiceRegistry.getInstace();
    	serviceRegistry.registerServer(ServiceRegistry.class,"1270.0.0.1:12315","40");
    	serviceRegistry.registerServer(ServiceRegistry.class,"1270.0.0.1:12314","60");
    	
    	Thread.sleep(100000);
    }
    
}