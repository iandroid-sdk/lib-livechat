package com.iandroid.allclass.lib_livechat.base;

/**
 * created by wangkm
 * on 2020/8/18.
 */
public interface IBaseChatAction {
    //开始认证
    public void auth();
    //认证结束
    public void authBack(boolean success);
    //生成认证串
    public Object getAuthCode();
}
