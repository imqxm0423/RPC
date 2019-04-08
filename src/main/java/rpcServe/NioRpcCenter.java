package rpcServe;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import rpcHandler.NioHandler;

import java.util.HashMap;

/**
 * author: qxm
 * time: 2019/4/8:22:47
 * remark:NIO服务中心实现
 **/
public class NioRpcCenter implements RpcServe {

    private static final HashMap<String, Class<?>> registers = new HashMap<String, Class<?>>();

    private static int port;

    public NioRpcCenter(int port) {
        this.port = port;
    }

    /**
     * 此方法用于注册中心绑定接口和实现
     *
     * @param serviceInterface 接口
     * @param impl             实现
     */
    public void register(Class serviceInterface, Class impl) {
        registers.put(serviceInterface.getName(),impl);
    }

    /**
     * 启动NIO,用netty实现
     */
    public void start() {
        ServerBootstrap serverBootstrap = null;
        EventLoopGroup bossGoup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGoup,workerGroup).channel(NioServerSocketChannel.class).localAddress(port).option(ChannelOption.SO_BACKLOG,128).
                    childOption(ChannelOption.SO_KEEPALIVE,true).childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                    pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast("encoder",new ObjectEncoder());
                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new NioHandler(registers));
                }
            });
            System.out.println("nio client start listern at port" + port);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGoup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
