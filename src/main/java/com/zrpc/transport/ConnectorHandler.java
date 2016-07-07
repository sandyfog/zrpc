package com.zrpc.transport;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zrpc.core.RpcFutureUtil;
import com.zrpc.core.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConnectorHandler extends  SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectorHandler.class);
	private RpcFutureUtil futureUtil;

   public ConnectorHandler(RpcFutureUtil futureUtil){
	   this.futureUtil=futureUtil;
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
        futureUtil.notifyRpcException(new Exception(cause));
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		
		futureUtil.notifyRpcMessage(msg);
		
	}
}
