package com.zrpc.transport;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zrpc.core.RpcRequest;
import com.zrpc.core.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AcceptorHandler extends  SimpleChannelInboundHandler<RpcRequest> {

   private static final Logger LOG = LoggerFactory.getLogger(AcceptorHandler.class);
   private NettyRpcAcceptor acceptor;
    
    public   AcceptorHandler(NettyRpcAcceptor acceptor){
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
    	LOG.info("Unexpected exception from downstream.", cause);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
		
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
