package com.zrpc.core.codec;

import java.io.IOException;

import com.zrpc.core.RpcConfig;

public class RpcCodec {
	
	private Serializer  serialization;
	
	private  RpcCodec(){
		serialization =  RpcConfig.SERIALIZER;
	}
	
	byte[] encode(Object obj) throws IOException{
		
		return serialization.serialize(obj);
	}
	
	public <T> T decode(byte[] bytes,Class<T> clazz) throws IOException{
		
		return serialization.deserialize(bytes,clazz);
	}
	
	public static RpcCodec getInstace(){
		return Holder.INSTNACE;
	}
	
	private static class Holder  {
		 public static final RpcCodec INSTNACE = new RpcCodec();
	}
	
}
