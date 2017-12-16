package me.atomishere.RPIBotSever;

import com.dexterind.gopigo.behaviours.Motion;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by archieoconnor on 16/12/17.
 */
public class TelnetServerHandler extends SimpleChannelInboundHandler<Integer> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Integer req) throws Exception {
        Integer response;
        boolean close = false;

        Motion motion = Motion.getInstance();
        if(req.equals(0x00)) {
            motion.stop();
            response = 0x08;
        } else if(req.equals(0x01)) {
            motion.forward(true);
            response = 0x08;
        } else if(req.equals(0x02)) {
            motion.backward(true);
            response = 0x08;
        } else if(req.equals(0x03)) {
            motion.right();
            response = 0x08;
        } else if(req.equals(0x04)) {
            motion.left();
            response = 0x08;
        } else if(req.equals(0x05)) {
            motion.increaseSpeed();
            response = 0x08;
        } else if(req.equals(0x06)) {
            motion.decreaseSpeed();
            response = 0x08;
        } else if(req.equals(0x07)) {
            response = 0x08;
            close = true;
        } else {
            response = 0x09;
        }

        ChannelFuture future = channelHandlerContext.write(response);

        if(close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
