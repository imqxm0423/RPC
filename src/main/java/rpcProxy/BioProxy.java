package rpcProxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * author: qxm
 * time: 2019/4/8:22:38
 * remark:bio方式的动态代理
 **/
public class BioProxy {

    public static Object getBioProxy(final Class<?> serviceInterface, final String ip, final int port) {
        return Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //调用方采用Socket

                        Socket socket = null;

                        ObjectOutputStream output = null;

                        ObjectInputStream input = null;

                        try {

                            //按照顺序写对应的数据

                            socket = new Socket();

                            socket.connect(new InetSocketAddress(ip, port));

                            output = new ObjectOutputStream(socket.getOutputStream());

                            output.writeUTF(serviceInterface.getName());

                            output.writeUTF(method.getName());

                            output.writeObject(method.getParameterTypes());

                            output.writeObject(args);

                            input = new ObjectInputStream(socket.getInputStream());

                            return input.readObject();

                        } finally {
                            if (socket != null) socket.close();

                            if (output != null) output.close();

                            if (input != null) input.close();
                        }
                    }
                });
    }
}
