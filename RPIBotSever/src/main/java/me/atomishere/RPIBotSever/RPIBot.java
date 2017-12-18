package me.atomishere.RPIBotSever;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.hopding.jrpicam.RPiCamera;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import marytts.LocalMaryInterface;

import java.io.FileInputStream;
import java.util.Collections;

public class RPIBot {
    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int port = Integer.parseInt(System.getProperty("port", SSL? "8992" : "8023"));
    private static RPIBot instance;
    private final PictureManager pictureManager;
    private final Vision vision;

    private RPIBot(Vision vision, PictureManager pictureManager) {
        this.pictureManager = pictureManager;
        this.vision = vision;
    }

    public static void main(String[] args) throws Exception {
        final SslContext sslCtx;
        if(SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        } else {
            sslCtx = null;
        }

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();


        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("MyProject-1234.json"))
                .createScoped(Collections.singleton(VisionScopes.CLOUD_VISION));

            Vision vision = new Vision.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("RPIBot").build();

        RPIBot instance = new RPIBot(vision , new PictureManager(new RPiCamera(System.getProperty("user.dir")), new LocalMaryInterface()));

        RPIBot.instance = instance;

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new TelnetSeverInitalizer(sslCtx));

            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static RPIBot getInstance() {
        return instance;
    }

    public PictureManager getPictureManager() {
        return pictureManager;
    }

    public Vision getVision() {
        return vision;
    }
}
