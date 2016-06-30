package com.zrpc.transport;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zrpc.core.RpcRequest;
import com.zrpc.core.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConnectorHandler extends  SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = Logger.getLogger(
    		ConnectorHandler.class.getName());
   private NettyRpcAcceptor acceptor;
    
    public   ConnectorHandler(NettyRpcAcceptor acceptor){
    	this.acceptor=acceptor;
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	System.out.println("channelUnregistered");
        ctx.fireChannelUnregistered();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the first message if this handler is a client-side handler.
    	System.out.println("channelActive");
    }

    

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.INFO,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
		
		System.out.println("server  recv "+msg);
		processRequest(ctx, msg);	
		
	}
	
	private void processRequest(final ChannelHandlerContext ctx,final RpcRequest request){
		//request 预处理，判断是否是心跳。。
		//todo
		acceptor.getProcessor().submit(new  Runnable() {
			public void run() {
				hand(ctx,request);
			}
		});	
	}
	
	public void hand( final ChannelHandlerContext ctx,RpcRequest request){
		
		Object obj = acceptor.getProcessor().findService(request.getClassName());
		if(obj!=null) {
			RpcResponse response = new RpcResponse();
			response.setRequestId(request.getRequestId());
			try {
				Method m = obj.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
				Object result = m.invoke(obj, request.getParameters());
				response.setResult(result);
			} catch (Throwable th) {
				th.printStackTrace();
				response.setError(th);
			}
			ctx.writeAndFlush(response);
		} else {
			throw new IllegalArgumentException("has no these class");
		}
		
	
	}
}
