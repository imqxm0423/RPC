package rpcServe;

/**
 * 这是一个rpc服务中心，提供了2个方法，一个是注册，一个是启动
 */
public interface RpcServe {

    void register(Class serviceInterface, Class impl);//提供注册方法和实现

    void start();//启动监听
}
