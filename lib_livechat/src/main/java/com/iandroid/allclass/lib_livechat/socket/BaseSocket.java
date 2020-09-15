package com.iandroid.allclass.lib_livechat.socket;

import android.text.TextUtils;
import android.util.Log;

import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

/**
 * created by wangkm
 * on 2020/8/6.
 */
public class BaseSocket {
    protected String TAG = this.getClass().getSimpleName();
    protected Socket socket;
    private List<String> hosts;//服务器列表
    private int hostIndex = 0;
    private IEmitterCallBack iEmitterCallBack;
    private Config config;
    private static long sSid;

    public BaseSocket(IEmitterCallBack iEmitterCallBack) {
        this.iEmitterCallBack = iEmitterCallBack;
    }

    public void login(Config config) throws LoginException {
        TAG = !TextUtils.isEmpty(config.tag()) ? config.tag() : TAG;
        connect(config);
    }

    public void logout() {
        Log.d(TAG, "[logout]" + this.getClass().getSimpleName());
        if (socket == null) return;
        socket.disconnect();
        socket = null;
    }

    public boolean isConnected() {
        if (socket == null) return false;
        return socket.connected();
    }

    /**
     * 连接socket
     *
     * @param config
     * @return
     */
    public void connect(Config config) throws LoginException {
        hosts = new ArrayList<>();
        if (config.hosts() != null)
            hosts.addAll(config.hosts());
        if (!TextUtils.isEmpty(config.host()))
            hosts.add(config.host());

        if (hosts.size() == 0) throw new LoginException("hosts 不能为空");

        this.config = config;
        IO.Options opts = new IO.Options();
        opts.transports = new String[]{WebSocket.NAME};
        opts.reconnectionAttempts = config.reconnectionAttempts();
        opts.reconnectionDelay = config.reconnectionDelay();
        opts.reconnectionDelayMax = config.reconnectionDelayMax();
        opts.secure = true;
        opts.sslContext = SocketUtils.getSSLContext();

        String srvUrl = getConnectUrl();
        if (TextUtils.isEmpty(srvUrl)) throw new LoginException("hosturl is null");
        try {
            Log.d(TAG, "连接状态机:" + srvUrl);
            socket = null;
            socket = IO.socket(getConnectUrl(), opts);
            for (String event : config.event_list()) {
                if (!TextUtils.isEmpty(event)) socket.on(event, new SocketEmiter(event));
            }
            socket.connect();
        } catch (URISyntaxException e) {
        }
    }

    /**
     * 支持多url重试机制
     *
     * @return
     */
    private String getConnectUrl() {
        hostIndex = Math.min(hostIndex, hosts.size() - 1);
        String url = hosts.get(hostIndex);
        hostIndex = Math.min(hostIndex + 1, hosts.size() - 1);
        return url;
    }

    public class SocketEmiter implements Emitter.Listener {
        private String event;

        public SocketEmiter(String event) {
            this.event = event;
        }

        @Override
        public void call(Object... args) {
            if (Config.enableLog()) {
                String data = "";
                for (Object obj : args) {
                    data += (obj == null ? "null " : obj.toString());
                }
                Log.d(TAG, "[recv][" + BaseSocket.this.getClass().getSimpleName() + "][ChatCon->" + config.isRoomChatConnection() + "]event:" + event + ", data:" + data);
            }
            if (iEmitterCallBack != null) iEmitterCallBack.onSocketReceive(event, args);
        }
    }

    /**
     * 发送数据
     *
     * @param event
     * @param args
     * @return
     */
    public boolean send(String event, final Object... args) {
        if (args == null || args.length == 0)
            return false;
        if (socket == null || !isConnected()) {
            return false;
        }
        if (Config.enableLog()) {
            String data = "";
            for (Object obj : args) {
                data += (obj == null ? "null " : obj.toString());
            }
            Log.d(TAG, "[send][" + BaseSocket.this.getClass().getSimpleName() + "][ChatCon->" + config.isRoomChatConnection() + "]event:" + event + ",data:" + data);
        }
        socket.emit(event, args);
        return true;
    }


    /**
     * 获取当前socket连接配置信息
     *
     * @return
     */
    public Config getConfig() {
        return config;
    }

    public static String genChatTransactionId(String head) {
        if (sSid == 0) {
            Date now = new Date();
            sSid = now.getTime();
        }
        sSid++;
        if (head == null)
            head = "";
        return head + sSid;
    }
}
