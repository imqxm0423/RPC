package interfaceAndImpl;

/**
 * author: qxm
 * time: 2019/4/8:22:43
 * remark:
 **/
public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        return "hello rpc ," + name;
    }
}
