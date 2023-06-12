package com.sprintray.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.GsonUtils;
import com.sprintray.ota.service.OTARequest;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpdateService extends Service {
    private static final String TAG = "UpdateService";


    private IOTAInterface iotaInterface;//定义AIDL


    /**
     * Whether it is automatic detection
     */
    private String checkModel = "";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");


        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.sprintray.ota", "com.sprintray.ota.service.SprintRayOTAService"));
        bindService(intent, mConnection, BIND_AUTO_CREATE);




    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: ");


        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        return super.onStartCommand(intent, flags, startId);
    }



    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {

        Log.d(TAG, "startForegroundService: ");


        return super.startForegroundService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }


        if (iotaInterface != null && iotaInterface.asBinder().isBinderAlive()) {
            try {
                iotaInterface.unregisterReceiveListener(iMessageListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


            iotaInterface = null;
        }
        unbindService(mConnection);


    }




    @Subscribe(threadMode = ThreadMode.MAIN ,sticky = true)
    public void onEvent(UpdateEntity updateEntity){
        try {

            if (iotaInterface == null) {

                //判断是否连接成功，如果没有重新连接
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.sprintray.ota", "com.sprintray.ota.service.SprintRayOTAService"));
                bindService(intent, mConnection, BIND_AUTO_CREATE);

                Observable.timer(2500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Throwable {
                                EventBus.getDefault().post(updateEntity);
                            }
                        });
            } else {
                if (updateEntity.getKey().equals(UpdateEvent.ACTION_CHECK_UPDATE)) {
                    checkModel = (String) updateEntity.getSubValue();
                    Log.d(TAG, "UpdateEvent.ACTION_CHECK_UPDATE: " + checkModel);
                    OTARequest updateRequest = (OTARequest) updateEntity.getValue();

                    if (updateRequest == null) {
                        return;
                    }

                    iotaInterface.requestVersionJson(GsonUtils.toJson(updateRequest));
                } else if (updateEntity.getKey().equals(UpdateEvent.ACTION_DOWNLOAD)) {
                    String response = (String) updateEntity.getValue();
                    if (response == null || response.equals("")) {
                        return;
                    }
                    iotaInterface.requestDownload(response);
                } else if (updateEntity.getKey().equals(UpdateEvent.ACTION_INSTALL)) {
                    iotaInterface.install();
                } else if (updateEntity.getKey().equals(UpdateEvent.ACTION_CHECK_STATE)) {
                    iotaInterface.getState();
                } else if (updateEntity.getKey().equals(UpdateEvent.ACTION_STOP)) {
                    iotaInterface.stop();
                } else if (updateEntity.getKey().equals(UpdateEvent.ACTION_SEND_MAN_STATE)) {
                    String value = (String) updateEntity.getValue();
                    iotaInterface.sendMainState(value);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
            Log.d(TAG, "onBindingDied: " + name);


            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.sprintray.ota", "com.sprintray.ota.service.SprintRayOTAService"));
            bindService(intent, mConnection, BIND_AUTO_CREATE);


        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, "onNullBinding: " + name);
            ServiceConnection.super.onNullBinding(name);
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iotaInterface = IOTAInterface.Stub.asInterface(iBinder);

            Log.d(TAG, "onServiceConnected: ");

            Log.d(TAG, "onServiceConnected: " + (iotaInterface == null));

            try {
                iotaInterface.asBinder().linkToDeath(mDeathRecipient, 0);
                iotaInterface.registerReceiveListener(iMessageListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.d(TAG, "onServiceDisconnected: ");
        }
    };


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "IBinder.DeathRecipient() binderDied:  "+(iotaInterface==null));
            if (iotaInterface == null) {
                return;
            }
            iotaInterface.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iotaInterface = null;
        }


    };


    private IMessageListener iMessageListener = new IMessageListener.Stub() {
        @Override
        public void onOTAMessage(OTAMessage otaMessage) throws RemoteException {



            if (otaMessage.getMessageType() == MessageType.MESSAGE_VERSION_RESPONSE) {
                EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_CHECK_UPDATE_RESPONSE, otaMessage, checkModel));
            } else if (otaMessage.getMessageType() == MessageType.MESSAGE_GET_STATE) {
                EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_CHECK_STATE_RESULT, otaMessage));
            } else if (otaMessage.getMessageType() == MessageType.MESSAGE_SUCCESS) {
                if (otaMessage.getOtaState() == OTAState.EVENT_DOWNLOAD_SUCCESS) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_DOWNLOAD_SUCCESS, otaMessage));
                } else if (otaMessage.getOtaState() == OTAState.EVENT_INSTALL_SUCCESS) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_INSTALL_SUCCESS, otaMessage));
                }
            } else if (otaMessage.getMessageType() == MessageType.MESSAGE_PROGRESS) {
                if (otaMessage.getOtaState() == OTAState.STATE_INSTALLING) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_INSTALL_PROGRESS, otaMessage));
                } else if (otaMessage.getOtaState() == OTAState.STATE_DOWNLOADING) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_DOWNLOAD_PROGRESS, otaMessage));
                }
            } else if (otaMessage.getMessageType() == MessageType.MESSAGE_ERROR) {
                if (otaMessage.getOtaState() == OTAState.EVENT_DOWNLOAD_FAILED) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_DOWNLOAD_FAILED, otaMessage));
                } else if (otaMessage.getOtaState() == OTAState.EVENT_INSTALL_FAILED) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_INSTALL_FAILED, otaMessage));
                } else if (otaMessage.getOtaState() == OTAState.EVENT_REQUEST_FAILED) {
                    EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_CHECK_FAILED, otaMessage));
                }
            } else if (otaMessage.getMessageType() == MessageType.MESSAGE_NOTICE) {
                EventBus.getDefault().post(new UpdateEntity(UpdateEvent.ACTION_NOTICE, otaMessage));

            }

        }
    };





}
