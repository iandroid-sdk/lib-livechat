package com.iandroid.allclass.bean;

/**
 * created by wangkm
 * on 2020/8/19.
 */
public class RoomInfo {
    public String pifd;
    public String live_id;
    public String live_key;
    public String from;
    public String from_seq;
    public String channel_id;
    public String area;

    public RoomInfo(String pifd, String live_id, String live_key) {
        this.pifd = pifd;
        this.live_id = live_id;
        this.live_key = live_key;
    }
    //2020-08-19 13:49:39.552 13917-14627/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=RoomTrace{from='', from_seq='', channel_id='', area_id='null'}, pfid='3687493', live_key='Fm3H1i', live_id='3687493Y09885Iv4L', prs_id=''}
//2020-08-19 13:48:10.725 12231-13488/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='1797163', live_key='x8SaZm', live_id='1797163Y03848IEWQ', prs_id=''}
  //  2020-08-19 13:48:42.786 12231-13733/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='4098922', live_key='38RIQL', live_id='4098922Y12920acIr', prs_id=''}
//2020-08-19 13:49:04.999 12231-13888/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='1497127', live_key='c1J49K', live_id='1497127Aa10268eUM', prs_id=''}
    //2020-08-19 13:49:22.791 13917-14458/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='4722853', live_key='9hwsik', live_id='4722853Y08264o1sw', prs_id=''}
}
