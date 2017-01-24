package cn.marasoft.netty.server.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by jiangqi on 2017/1/24.
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage>{
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMMessage imMessage, ByteBuf byteBuf) throws Exception {
        
    }
}
