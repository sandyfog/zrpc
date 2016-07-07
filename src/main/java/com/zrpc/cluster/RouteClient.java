package com.zrpc.cluster;

import java.io.IOException;

import com.zrpc.core.RpcConnector;
import com.zrpc.core.RpcRequest;
import com.zrpc.core.RpcResponse;

public class RouteClient {
	private RpcConnector connector;
	public int weight;
	public int currentWeight;
	private  boolean down = false;
	
	public RouteClient(RpcConnector connector,int weight){
		this.connector = connector;
		this.weight = weight;
		this.currentWeight = 0;
		if (this.weight < 0) {
			this.down = true;
		} else {
			this.down = false;
		}
	}
	
	public RpcResponse invoke(RpcRequest requst) throws IOException{
		return connector.invoke(requst);
	}
	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}
	
	public String toString(){
		return connector +"\t"+weight;
	}
}
