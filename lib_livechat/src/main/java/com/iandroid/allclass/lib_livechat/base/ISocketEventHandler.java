package com.iandroid.allclass.lib_livechat.base;

import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

/**
 * created by wangkm
 * on 2020/8/11.
 */
public interface ISocketEventHandler {
    /**
     * 计算线程中回调，主要处理一些耗时、计算场景高的比如json解析等操作
     * 可以在该回调中针对event解析object[]中的数据，并将解析后的对象作为函数对象返回
     * @param event
     * @param original
     * @return 可以在该过程不对数据做任何解析，直接返回空
     */
    public Object onMsgParse(String event, Object[] original);

    /**
     * 主线程中回调，
     * @param event
     * @param originalData event下socket返回的原始数据
     * @param eventData  onMsgParse返回的数据
     */
    public void onReceiveMsg(String event, Object[] originalData, Object eventData);


    /**
     * main thread中回调
     * 通知socket连接及其认证状态
     * @param status
     */
    public void statusCallback(SocketEvent.enmSocketStatus status);
}
