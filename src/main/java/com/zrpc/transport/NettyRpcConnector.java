package com.zrpc.transport;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.zrpc.core.RpcConnector;
import com.zrpc.core.RpcContext;
import com.zrpc.core.RpcException;
import com.zrpc.core.RpcFuture;
import com.zrpc.core.RpcFutureUtil;
import com.zrpc.core.RpcRequest;
import com.zrpc.core.RpcResponse;
import com.zrpc.core.codec.RpcDecoder;
import com.zrpc.core.codec.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyRpcConnector implements RpcConnector {

	private String host;
	private int port;
	private Channel chanel;
    private RpcFutureUtil futureUtil = new RpcFutureUtil();
	public void init() {
		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
//									new LoggingHandler(LogLevel.INFO),
									new RpcEncoder(),
									new RpcDecoder(RpcResponse.class),
									new AcceptorHandler(futureUtil));
						}
					});

			// Start the client.
			ChannelFuture f = b.connect(host, port).sync();
			chanel = f.channel();
			// Wait until the connection is closed.
//			f.channel().closeFuture().sync();

		} catch (Exception e) {
			System.out.println("ssssssssssss");
			e.printStackTrace();
		} 
		
//		finally {
//			// Shut down the event loop to terminate all threads.
//			group.shutdownGracefully();
//		}
	}


	
	@Override
	public void setHost(String host) {
		this.host = host;

	}

	@Override
	public void setport(int port) {
		this.port = port;

	}

	@Override
	public void start() throws IOException {
		init();

	}

	@Override
	public RpcResponse invoke(RpcRequest request) throws IOException {
		
		try {
			RpcContext ctx = RpcContext.getContext();
			boolean async = ctx.isAsync();
			boolean oneway = ctx.isOneway();
			return send(request, oneway,async);
		} finally {
			RpcContext.removeContext();
		}
		
	}


	public RpcResponse  send(RpcRequest request,boolean oneway, boolean async){
		
		long mid = request.getRequestId();
		RpcFuture<Object> future = null;
		try {
		if (!oneway) {
			future = new  RpcFuture<Object>();
			futureUtil.setRpcFuture(mid, future);
		 }
		
		chanel.writeAndFlush(request);
		
		// One way request, client does not expect response
		if (oneway) { return null; }
		
		if (async) {
			// async and set future
			RpcContext.getContext().setFuture(future);
			return null;
		}else{
			// sync and wait response
			future.await(RpcContext.getContext().getRpcTimeoutInMillis(), TimeUnit.MILLISECONDS);
			return future.getResponse();
		}
		}catch (RpcException e) {
			throw e;
		} catch (IOException e) {
			throw new RpcException("network error", e); 
		} catch (TimeoutException e) {
			throw new RpcException("client timeout", e);
		} catch (Exception e) {
			throw new RpcException("unknown error", e);
		}
	}
}
