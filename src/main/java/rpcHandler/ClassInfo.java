package rpcHandler;

import lombok.Data;

import java.io.Serializable;

/**
 * author: qxm
 * time: 2019/4/8:23:01
 * remark:此类用于NIO传输具体的数据
 **/
@Data
public class ClassInfo implements Serializable {

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;
}
