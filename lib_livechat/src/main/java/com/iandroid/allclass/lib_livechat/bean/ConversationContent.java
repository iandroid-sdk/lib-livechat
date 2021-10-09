package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2020/8/18.
 */
public class ConversationContent {
    private String imgurl;//红包的用头像
    private String title;
    private int type;
    private String liveurl;
    private int msg_type = ChatMsgType.TEXT;   //图文
    private String content;//红包的话，就是祝福语；系统提示的描述文字
    private String gift_effect;
    private int gift_price;
    private int gift_id;

    public String getGift_effect() {
        return gift_effect;
    }

    public void setGift_effect(String gift_effect) {
        this.gift_effect = gift_effect;
    }

    public int getGift_price() {
        return gift_price;
    }

    public void setGift_price(int gift_price) {
        this.gift_price = gift_price;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public String getLiveurl() {
        return liveurl;
    }

    public void setLiveurl(String liveurl) {
        this.liveurl = liveurl;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
