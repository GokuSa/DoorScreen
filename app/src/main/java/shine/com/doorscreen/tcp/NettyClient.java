package shine.com.doorscreen.tcp;

/**
 * Created by Administrator on 2016/8/5.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import shine.com.doorscreen.app.AppEntrance;
import shine.com.doorscreen.util.IniReaderNoSection;

public final class NettyClient {
    private static final String TAG = "NettyClient";

    //    static  String HOST = System.getProperty("host", "172.168.1.55");
    static String HOST = "";
    static  int PORT =0;
//    static  int PORT = Integer.parseInt(System.getProperty("port", "5020"));
    //重连间隔设为5s.
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "20"));
    // 如果10s没收到服务器数据，则读超市，断开连接重连.
    static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "25"));
    private static final NettyClientHandler handler = new NettyClientHandler();
    private static NettyClient sNettyClient;

    private NettyClient() {}

    public synchronized static NettyClient getInstance() {
        if (sNettyClient == null) {
            sNettyClient = new NettyClient();
        }
        return sNettyClient;
    }

    public Bootstrap configureBootstrap(Bootstrap b) {
        return configureBootstrap(b, new NioEventLoopGroup());
    }

    public Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
        IniReaderNoSection inir = new IniReaderNoSection(AppEntrance.ETHERNET_PATH);
        HOST = inir.getValue("commuip");
        PORT =Integer.parseInt(inir.getValue("commuport"));
        b.group(g)
                .channel(NioSocketChannel.class)
                .remoteAddress(HOST, PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // Decoders
                        pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(1024 * 1024 * 1024));
                        pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                        //encoders
                        pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast("lineEncoder", new LineEncoder(LineSeparator.UNIX, CharsetUtil.UTF_8));
                        pipeline.addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), handler);
                    }
                });

        return b;
    }

    /*
    * 连接服务器并监听结果
    * 在Handler中断线重连时使用*/
    public void connect(Bootstrap b) {
        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    handler.startTime = -1;
                    handler.println("Failed to connect: " + future.cause());

                }
            }
        });
    }

    public void addListener(int requestCode,DataReceiveListener dataReceiveListener) {
        handler.addRequestListener(requestCode,dataReceiveListener);
    }

    public void removeListener(int requestCode) {
        handler.removeRequest(requestCode);
    }

    public void removeAllListener() {
        handler.removeAll();
    }
}
