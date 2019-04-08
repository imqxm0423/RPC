package rpcHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * author: qxm
 * time: 2019/4/8:22:59
 * remark:此类继承后用于具体的实现
 **/
public class NioHandler extends ChannelInboundHandlerAdapter {

    private HashMap<String, Class<?>> registers;

    public NioHandler(HashMap<String, Class<?>> registers) {
        this.registers = registers;
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo info = (ClassInfo) msg;
        String serviceName = info.getServiceName();
        Class<?> impl = registers.get(serviceName);
        if (impl == null) {
            throw new ClassNotFoundException(serviceName + " not found");
        }
        String methodName = info.getMethodName();
        Class<?>[] parameterTypes = info.getParameterTypes();
        Method method = impl.getMethod(methodName, parameterTypes);
        Object[] args = info.getArgs();
        Object result = method.invoke(impl.newInstance(), args);
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
