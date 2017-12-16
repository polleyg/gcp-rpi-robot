package me.atomishere.RPIBotSever;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Created by archieoconnor on 16/12/17.
 */
public class TelnetSeverInitalizer extends ChannelInitializer<SocketChannel> {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

    private final SslContext sslCtx;

    public TelnetSeverInitalizer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        if(sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);

        pipeline.addLast(SERVER_HANDLER);
    }
}
