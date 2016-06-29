package com.zrpc.transport;

import java.io.IOException;

import com.zrpc.core.RpcAcceptor;
import com.zrpc.core.RpcProcessor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class  NettyRpcAcceptor implements RpcAcceptor{
	
    private String host;
    private int port;
    private RpcProcessor processor;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
  	
	public void init() throws IOException {
		 // Configure the server.
         bossGroup = new NioEventLoopGroup();
         workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 100)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(
                             new LoggingHandler(LogLevel.INFO),
                    		  new ObjectEncoder(),
                              new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                              new ObjectServerHandler(NettyRpcAcceptor.this));
                 }
             });

            // Start the server.
            ChannelFuture f =  b.bind(host,port).sync();
            System.out.println("started and listen on");
            // Wait until the server socket is closed.
          //  f.channel().closeFuture().sync();
        }catch(Exception e) {
        	e.printStackTrace();
        } 
//        finally {
//            // Shut down all event loops to terminate all threads.
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
	}
	
	@Override
	public void start() throws IOException {
		this.init();
	}


	@Override
	public void stop() throws IOException {
		 bossGroup.shutdownGracefully();
         workerGroup.shutdownGracefully();
	}


	@Override
	public void setHost(String host) {
		this.host=host;
	}

	@Override
	public void setport(int port) {
		this.port=port;
	}



	public RpcProcessor getProcessor() {
		return processor;
	}


	@Override
	public void setProcessor(RpcProcessor processor) {
		this.processor=processor;
	}

}
