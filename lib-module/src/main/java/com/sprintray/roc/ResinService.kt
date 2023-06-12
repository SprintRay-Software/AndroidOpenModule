package com.sprintray.roc

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import org.json.JSONObject
import java.util.concurrent.TimeUnit


// Client side of Resin on Cloud Module; Service for the Capricorn App
class ResinService: Service() {
    var iRoCService: IRoCInterface? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        connectToRemoteService()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }

        if (iRoCService != null && iRoCService?.asBinder()?.isBinderAlive == true) {
            try {
                iRoCService?.unregisterReceiveListener(mCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            iRoCService = null
        }
        unbindService(mConnection)
    }

    private fun connectToRemoteService() {
        val intent = Intent("service")
        intent.component = ComponentName("com.sprintray.roc", "com.sprintray.roc.RoCService")
        Log.d(TAG, "connectToRemoteService: ${intent.action} :: ${intent.`package`}")
        val boo = bindService(intent, mConnection, BIND_AUTO_CREATE)
        Log.i(TAG, "service is bound: $boo")
    }

    private val mCallback = object : IRocListener.Stub() {
        override fun onCallback(message: String?) {
            Log.d(TAG, "onUpdateNotify: $message")

            message?.let {
                var success = JSONObject(message).getBoolean("request");
                Log.d(TAG, "onCallback: $success")
                EventBus.getDefault().post(ResinUpdatedEvent(success))
            }
        }
    }

    val mConnection = object : ServiceConnection {
        override fun onBindingDied(name: ComponentName) {
            super.onBindingDied(name)

            Log.d(TAG, "onBindingDied: $name")
            val intent = Intent()
            intent.component =
                ComponentName("com.sprintray.roc", "com.sprintray.roc.RoCService")
            bindService(intent, this, BIND_AUTO_CREATE)
        }

        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected: $className + $service")
            // for an AIDL interface, this gets an instance of the IRoCInterface, which is used to call on the service
            iRoCService = IRoCInterface.Stub.asInterface(service)
            //try {
                Log.d(TAG, "onServiceConnected: try?")
                iRoCService?.asBinder()?.linkToDeath(mDeathRecipient, 0)
                iRoCService?.registerReceiveListener(mCallback)
            //} catch (e :Exception){
                Log.d(TAG, "onServiceConnected: catch")
                //e.printStackTrace()
            //}

        }

        override fun onServiceDisconnected(className: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: Service disconnected")
            //iRoCService?.unregisterReceiveListener(mCallback)
            iRoCService = null
        }
    }

    private val mDeathRecipient: IBinder.DeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            Log.d(TAG, "IBinder.DeathRecipient() binderDied:  " + (iRoCService == null))
            if (iRoCService == null) { return }
            iRoCService!!.asBinder().unlinkToDeath(this, 0)
            iRoCService = null
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: ResinEvent) {
        try {
            if (iRoCService == null) {
                //判断是否连接成功，如果没有重新连接
                val intent = Intent("service")
                intent.component = ComponentName("com.sprintray.roc", "com.sprintray.roc.RoCService")
                bindService(intent, mConnection, BIND_AUTO_CREATE)

                Observable.timer(2500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe { aLong ->
                        EventBus.getDefault().post(e)
                    }
            } else {
                when (e.message) {
                    ResinRequest.RESIN_UPDATE -> iRoCService?.updateResin()
                    ResinRequest.RESIN_GET_ALL -> {
                        iRoCService?.allResin // a List of String of resin names
                    }
                    ResinRequest.RESIN_GET_BY_ID -> {
                        iRoCService?.getResinById(e.id) // a ResinProfileLayerModel: ResinProfileLayerModel(washTime=180, rinseTime=180, dryTime=180)
                    }

                    ResinRequest.RESIN_GET_PROFILES -> {
                        iRoCService?.resinProfiles // a list of ResinModel
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ResinService"
    }
}