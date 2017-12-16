package me.atomishere.rpibotclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by archieoconnor on 16/12/17.
 */
public class TelnetClientHandler extends SimpleChannelInboundHandler<Integer> {

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Integer integer) throws Exception {
        if(integer.equals(0x09)) {
            System.err.println("The client sent an invalid packet to the sever!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
