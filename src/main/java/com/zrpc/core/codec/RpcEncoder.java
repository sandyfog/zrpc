package com.zrpc.core.codec;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<Serializable> {
   private RpcCodec  codec;
   
   public RpcEncoder(){
	   codec = RpcCodec.getInstace();
   }
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
		byte[] bytes = codec.encode(msg);
		out.writeInt(bytes.length);
		out.writeBytes(bytes);
	}

}
