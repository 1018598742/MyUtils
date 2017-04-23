package com.fta.myapplication.rxbus;

/**
 * 文件描述： RxBus 传递的类型
 * 作者： Created by fta on 2017/4/22
 * 来源：
 */

public class RxBusBaseMessage {
    private int code;
    private Object object;
    public RxBusBaseMessage(int code, Object object){
        this.code=code;
        this.object=object;
    }
    public RxBusBaseMessage(){}

    public int getCode() {
        return code;
    }

    public Object getObject() {
        return object;
    }
}
