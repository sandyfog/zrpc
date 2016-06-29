package com.zrpc.example;

public class HelloServiceImpl implements HelloService {  
	static {
//		System.out.println(" init ...");
	}
	  
    public String hello(String name) {  
    	System.out.println("执行rpc调用。。。");
        return "Hello " + name;  
    }  
  
}  