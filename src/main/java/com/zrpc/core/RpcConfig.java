package com.zrpc.core;

import com.zrpc.core.codec.Hessian2Serializer;
import com.zrpc.core.codec.Serializer;

public class RpcConfig {

	public static String DEFAULT_VERSION = "0.0";
	public static int RPC_TIMEOUT = 30000;
	public static int NODE_ID = 1;
	public static int EXECUTOR_THREAD_COUNT = Runtime.getRuntime().availableProcessors()*2;
//	public static Serializer SERIALIZER = new JdkSerializer() ;
//	public static Serializer SERIALIZER = KryoSerializer.getInstance() ;
	public static Serializer SERIALIZER = new Hessian2Serializer() ;
//	public static Serializer SERIALIZER = new ProtostuffSerializer() ;
	
}
