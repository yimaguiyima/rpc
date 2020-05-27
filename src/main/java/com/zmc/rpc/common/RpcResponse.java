package com.zmc.rpc.common;

public class RpcResponse {
    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError() {
        // TODO Auto-generated method stub
        return error != null;
    }

    public Object getResult() {
        // TODO Auto-generated method stub
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
