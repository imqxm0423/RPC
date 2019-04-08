package rpcProxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import rpcHandler.ClassInfo;
import rpcHandler.NioResultHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * author: qxm
 * time: 2019/4/8:23:08
 * remark:
 **/
public class NioProxy {

    public static Object getProxy(final Class<?> serviceInterface, final String ip, final int port) {
        return Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        EventLoopGroup group = new NioEventLoopGroup();
                        Bootstrap bootstrap = new Bootstrap();
                        final NioResultHandler resultHandler = new NioResultHandler();
                        ClassInfo info = new ClassInfo();
                        info.setServiceName(serviceInterface.getName());
                        info.setMethodName(method.getName());
                        info.setParameterTypes(method.getParameterTypes());
                        info.setArgs(args);
                        try {
                            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true).handler(
                                    new ChannelInitializer<SocketChannel>() {
                                        protected void initChannel(SocketChannel ch) throws Exception {
                                            ChannelPipeline pipeline = ch.pipeline();
                                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                                            pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                                            pipeline.addLast("encoder",new ObjectEncoder());
                                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                            pipeline.addLast("handler",resultHandler);
                                        }
                                    }
                            );
                            ChannelFuture future = bootstrap.connect(ip, port).sync();
                            future.channel().writeAndFlush(info);
                            future.channel().closeFuture().sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            group.shutdownGracefully();
                        }
                        return resultHandler.getResponse();
                    }
                });
    }
}
