package com.iandroid.allclass.lib_livechat.socket;

/**
 * created by wangkm
 * on 2020/8/11.
 */
public interface IEmitterCallBack {
    public void onSocketReceive(String event, Object... args);
}
