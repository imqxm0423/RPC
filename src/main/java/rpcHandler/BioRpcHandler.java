package rpcHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

/**
 * author: qxm
 * time: 2019/4/8:22:31
 * remark:bio的handler,用于解析接口并反射执行任务
 **/
public class BioRpcHandler implements Runnable {

    private Socket socket;

    private HashMap<String, Class<?>> registers;

    public BioRpcHandler(Socket socket, HashMap<String, Class<?>> registers) {
        this.socket = socket;
        this.registers = registers;
    }

    /**
     * 此方法执行具体的解析
     */
    public void run() {
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            //读取的方式是和写的方式保持一致的

            String serviceName = input.readUTF();

            String methodName = input.readUTF();

            Class<?>[] paremeterTypes = (Class<?>[]) input.readObject();

            Object[] args = (Object[]) input.readObject();

            Class<?> impl = registers.get(serviceName);

            if (impl == null) {

                throw new ClassNotFoundException(serviceName + "not found");

            }

            Method method = impl.getMethod(methodName, paremeterTypes);

            Object result = method.invoke(impl.newInstance(), args);

            output = new ObjectOutputStream(socket.getOutputStream());

            //执行完后再把结果输出回去

            output.writeObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
