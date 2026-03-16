package com.java.birdme.bean;

public class ReturnResp {
    private int code;
    private String msg;
    private Object data;

    public ReturnResp(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnResp(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ReturnResp success(Object data){
        return new ReturnResp(200, "ok", data);
    }

    public static ReturnResp success(){
        return new ReturnResp(200, "ok");
    }

    public static ReturnResp fail(String msg){
        return new ReturnResp(500, msg);
    }

    public static ReturnResp fail(){
        return new ReturnResp(500, "fail");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
