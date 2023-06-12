package com.sprintray.service.proxy;

public abstract class ProxyClient implements ProxyInterface {

    private String tag;
    public  void setTag(String tag){
        this.tag = tag;
    };

    public String getTag() {
        return tag;
    }
}
