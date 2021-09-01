package com.iandroid.allclass.lib_livechat.bean;

import java.io.Serializable;

/**
 * created by wangkm
 * on 2021/9/1.
 */
class JumpAble implements Serializable {
    int id = 0;
    Object param;

    public JumpAble(int id, Object param) {
        this.id = id;
        this.param = param;
    }

    public JumpAble() {
    }
}
