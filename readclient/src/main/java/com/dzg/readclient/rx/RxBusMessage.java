package com.dzg.readclient.rx;

/**
 * Created by quantan.liu on 2017/3/10.
 */

public class RxBusMessage {


    private  int code;
    private Object object;
    public RxBusMessage(){}
    public RxBusMessage(int code, Object object){
        this.code=code;
        this.object=object;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }
}
