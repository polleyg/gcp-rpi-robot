package me.atomishere.rpibotclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Created by archieoconnor on 16/12/17.
 */
public class RpiBotClient {
    public static final boolean SSL = System.getProperty("SSL") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8892" : "8023"));
    private static RpiBotClient instance;
    private static Integer data = null;

    private IRobotControls robotControls;

    private RpiBotClient(IRobotControls robotControls) {
        this.robotControls = robotControls;
        instance = this;
    }

    /**
     * Opens a connection to the server and create an instance of the client.
     *
     * @throws Exception if the client fails to connect.
     */
    public static void connect() throws Exception {
        final SslContext sslCtx;
        if(SSL) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer(sslCtx));

            Channel ch = b.connect(HOST, PORT).sync().channel();

            ChannelFuture lastWriteFuture;
            new RpiBotClient(new RobotControls());
            for(;;) {
                if(data == null) {
                    continue;
                }

                lastWriteFuture = ch.write(data);

                if(data.equals(0x09)) {
                    ch.closeFuture().sync();
                    break;
                }
            }

            if(lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * Get the instance of the client
     *
     * @return the client.
     */
    public static RpiBotClient getInstance() {
        return instance;
    }

    /**
     * Disconnect the client from the server
     */
    public void disconnect() {
        setData(0x09);
    }

    static void setData(Integer data) {
        RpiBotClient.data = data;
    }

    /**
     * Get main robot controls.
     *
     * @return  the robot controls
     * @see me.atomishere.rpibotclient.IRobotControls
     */
    public IRobotControls getRobotControls() {
        return robotControls;
    }
}
