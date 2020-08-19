package com.iandroid.allclass.lib_livechat.base;

/**
 * created by wangkm
 * on 2020/8/11.
 */
public class SocketEventData {
    private String event;
    private Object[] objects;
    private Object nextData;

    public SocketEventData(String event, Object[] objects) {
        this.event = event;
        this.objects = objects;
    }

    public Object getNextData() {
        return nextData;
    }

    public void setNextData(Object nextData) {
        this.nextData = nextData;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
