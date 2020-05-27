package com.zmc.rpc.common;

public class RpcRequest {
    private String requestId;
    private String methodName;
    private String className;
    private Class<?>[] parameterTypes;

    private Object[] parameters;


    public void setClassName(String name) {
        // TODO Auto-generated method stub
        this.className = name;
    }

    public String getClassName() {
        return className;
    }

    public void setMethodName(String name) {
        // TODO Auto-generated method stub
        this.methodName = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setRequestId(String string) {
        // TODO Auto-generated method stub
        this.requestId = string;
    }

    public String getRequestId() {
        // TODO Auto-generated method stub
        return requestId;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        // TODO Auto-generated method stub
        this.parameterTypes = parameterTypes;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameters(Object[] args2) {
        // TODO Auto-generated method stub
        this.parameters = args2;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "method: " + methodName + " className: " + className;
    }
}
