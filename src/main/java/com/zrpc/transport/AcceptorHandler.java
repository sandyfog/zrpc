package com.zrpc.transport;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.zrpc.core.RpcFutureUtil;
import com.zrpc.core.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AcceptorHandler extends  SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = Logger.getLogger(
            AcceptorHandler.class.getName());
	private RpcFutureUtil futureUtil;

   public AcceptorHandler(RpcFutureUtil futureUtil){
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
        logger.log(
                Level.INFO,
                "Unexpected exception from downstream.", cause);
        futureUtil.notifyRpcException(new Exception(cause));
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		
		futureUtil.notifyRpcMessage(msg);
		
	}
}
