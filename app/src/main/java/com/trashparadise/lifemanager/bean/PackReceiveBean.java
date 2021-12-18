package com.trashparadise.lifemanager.bean;

public class PackReceiveBean {
    String src;
    String json;

    public String getSrc() {
        return src;
    }

    public PackReceiveBean(String src, String json) {
        this.src = src;
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
