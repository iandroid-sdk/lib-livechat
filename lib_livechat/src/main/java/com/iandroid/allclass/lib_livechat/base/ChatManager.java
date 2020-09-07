package com.iandroid.allclass.lib_livechat.base;

import android.text.TextUtils;

import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.BaseSocket;
import com.iandroid.allclass.lib_livechat.socket.IEmitterCallBack;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.socket.client.Socket;

/**
 * created by wangkm
 * on 2020/8/6.
 */
public abstract class ChatManager implements IEmitterCallBack, IBaseChatAction {
    protected BaseSocket baseSocket;
    protected String TAG = ChatManager.this.getClass().getSimpleName();
    protected CompositeDisposable mDisposable;
    private Subject<Object> mMsgReceiveSubject = null;
    private ISocketEventHandler iSocketEventHandler;
    protected List<String> socket_event_list;

    public static RoomChatPresenter getRoomChat(ISocketEventHandler iSocketEventHandler) {
        return new RoomChatPresenter(iSocketEventHandler);
    }

    public static StateChatPresenter getStateChat(ISocketEventHandler iSocketEventHandler) {
        return new StateChatPresenter(iSocketEventHandler);
    }

    /**
     * 登入
     */
    public void login(Config config) throws LoginException {
        if (config == null) throw new LoginException("config is null");
        if (baseSocket == null) throw new LoginException("socket is null");

        complete();
        TAG = !TextUtils.isEmpty(config.tag()) ? config.tag() : TAG;
        mDisposable = new CompositeDisposable();
        createMsgReceiveDisposable();
        combineEvent(config);
        baseSocket.login(config);
    }

    /**
     * 登出
     */
    public void logout() {
        complete();
    }

    private void createMsgReceiveDisposable() {
        mMsgReceiveSubject = PublishSubject.create();
        mDisposable = new CompositeDisposable();
        Disposable disposable = mMsgReceiveSubject.observeOn(Schedulers.computation())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(@NonNull Object object) {
                        SocketEventData socketEventData = (SocketEventData) object;
                        socketEventData.setNextData(handleEventOnIOThread(socketEventData.getEvent(),
                                socketEventData.getObjects()));
                        return socketEventData;
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object object) {
                        SocketEventData socketEventData = (SocketEventData) object;
                        handleEventOnMainThread(socketEventData.getEvent(),
                                socketEventData.getObjects(),
                                socketEventData.getNextData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                    }
                });
        mDisposable.add(disposable);
    }

    protected Object handleEventOnIOThread(String event, Object[] original) {
        Object nextData = null;
        if (iSocketEventHandler != null)
            nextData = iSocketEventHandler.onMsgParse(event, original);
        return nextData;
    }

    protected void handleEventOnMainThread(String event, Object[] originalData, Object actionData) {
        if (iSocketEventHandler != null)
            iSocketEventHandler.onReceiveMsg(event, originalData, actionData);
    }

    @Override
    public void onSocketReceive(String event, Object... args) {
        switch (event) {
            case Socket.EVENT_CONNECT:
                auth();
                break;
            case SocketEvent.EVENT_AUTHENTICATED:
                authBack(true);
                break;
            case SocketEvent.EVENT_UNAUTHENTICATED:
                break;
            default:
                break;
        }
        if (mMsgReceiveSubject != null)
            mMsgReceiveSubject.onNext(new SocketEventData(event, args == null ? null : args.clone()));
    }

    @Override
    public void authBack(boolean success) {

    }

    private void complete() {
        if (baseSocket != null && baseSocket.isConnected()) baseSocket.logout();
        if (mMsgReceiveSubject != null)
            mMsgReceiveSubject.onComplete();
        if (mDisposable != null) mDisposable.dispose();
    }

    public boolean send(String event, final Object... args) {
        return baseSocket != null && baseSocket.send(event, args);
    }

    public ChatManager(ISocketEventHandler iSocketEventHandler) {
        this.iSocketEventHandler = iSocketEventHandler;
        baseSocket = new BaseSocket(this);
        socket_event_list = new ArrayList<>();
        socket_event_list.add(Socket.EVENT_CONNECT);
        socket_event_list.add(Socket.EVENT_CONNECT_ERROR);
        socket_event_list.add(Socket.EVENT_CONNECT_TIMEOUT);
        socket_event_list.add(Socket.EVENT_RECONNECT_ERROR);
        socket_event_list.add(Socket.EVENT_RECONNECT_FAILED);
        socket_event_list.add(Socket.EVENT_RECONNECT_ATTEMPT);
        socket_event_list.add(SocketEvent.EVENT_AUTHENTICATED);
        socket_event_list.add(SocketEvent.EVENT_UNAUTHENTICATED);
        socket_event_list.add(SocketEvent.EVENT_CLOSE);
    }

    private void combineEvent(Config config) {
        ArrayList<String> eventListInConfig = config.event_list();
        if (eventListInConfig == null) return;
        for (String event : eventListInConfig) {
            if (!TextUtils.isEmpty(event) && socket_event_list.indexOf(event) < 0)
                socket_event_list.add(event);
        }
        eventListInConfig.clear();
        eventListInConfig.addAll(socket_event_list);
    }

    protected Config getConfig() {
        return baseSocket != null ? baseSocket.getConfig() : null;
    }

    public boolean isConnected() {
        return baseSocket != null && baseSocket.isConnected();
    }
}