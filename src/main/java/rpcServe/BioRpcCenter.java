package rpcServe;

import rpcHandler.BioRpcHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: qxm
 * time: 2019/4/8:22:16
 * remark: bio实现的rpc注册中心
 **/
public class BioRpcCenter implements RpcServe {

    //BIO为阻塞IO，需要用线程去监听
    private static final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //可以简单的理解为注册中心
    private static final HashMap<String, Class<?>> registers = new HashMap<String, Class<?>>();

    private static int port;

    public BioRpcCenter(int port) {
        this.port = port;
    }

    /**
     * 此方法用于注册接口和实现类
     *
     * @param serviceInterface 接口
     * @param impl             实现
     */
    public void register(Class serviceInterface, Class impl) {
        registers.put(serviceInterface.getName(), impl);
    }

    /**
     * 此方法是用于启动BIO的RPC服务，并监控端口
     */
    public void start() {
        //BIO的服务中心使用的是ServeSocket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            System.out.println("bio client start listen at port:" +port);
            while (true){
                //监听到具体的socket后，用具体的任务去执行
                executors.submit(new BioRpcHandler(serverSocket.accept(),registers));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
               try {
                   serverSocket.close();
               }catch (IOException e){
                   e.printStackTrace();
               }
            }
        }
    }
}
