import interfaceAndImpl.HelloService;
import interfaceAndImpl.HelloServiceImpl;
import rpcProxy.BioProxy;
import rpcProxy.NioProxy;
import rpcServe.BioRpcCenter;
import rpcServe.NioRpcCenter;
import rpcServe.RpcServe;

/**
 * author: qxm
 * time: 2019/4/8:22:43
 * remark:主测试方法
 **/
public class MainTest {
    public static void main(String[] args) {
//        bioTest();
        nioTest();
    }

    private static void bioTest(){
        new Thread(new Runnable() {
            public void run() {
                RpcServe rpcServe = new BioRpcCenter(8099);
                rpcServe.register(HelloService.class, HelloServiceImpl.class);
                rpcServe.start();
            }
        }).start();

        HelloService service = (HelloService) BioProxy.getBioProxy(HelloService.class,"localhost",8099);
        String qxm = service.sayHello("qxm");
        System.out.println(qxm);
    }

    private static void nioTest(){
        new Thread(new Runnable() {
            public void run() {
                RpcServe serve = new NioRpcCenter(8899);
                serve.register(HelloService.class,HelloServiceImpl.class);
                serve.start();
            }
        }).start();

        HelloService service = (HelloService) NioProxy.getProxy(HelloService.class,"localhost",8899);
        String qxm_nio = service.sayHello("qxm nio");
        System.out.println(qxm_nio);

    }
}

