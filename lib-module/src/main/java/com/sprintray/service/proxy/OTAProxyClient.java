package com.sprintray.service.proxy;

import android.util.ArrayMap;
import android.util.Log;

import com.sprintray.ota.service.OTAMessage;

import java.util.ArrayList;
import java.util.List;

public class OTAProxyClient implements ProxyInterface {
    private static final String TAG = "OTAProxyClient";


    private static ArrayMap<String, ProxyClient> proxyMap = new ArrayMap<String, ProxyClient>();




    public static void addListener(ProxyClient proxyClient) {
        if (proxyMap != null) {
            if (!proxyMap.containsKey(proxyClient.getTag())) {
                proxyMap.put(proxyClient.getTag(), proxyClient);
            }
        }
    }

    public static void removeListener(ProxyClient proxyClient) {
        if (proxyMap != null) {
            if (proxyMap.containsKey(proxyClient.getTag())) {
                Log.d(TAG, "addListener: ");
                proxyMap.remove(proxyClient.getTag());
            }
        }
    }

    public static void destroy() {
        if (proxyMap != null) {
            proxyMap.clear();
        }
    }


    @Override
    public void onOTAMessage(OTAMessage otaMessage) {
        if (proxyMap.isEmpty()) {
            return;
        }
        for (ProxyClient proxyClient : proxyMap.values()) {
            proxyClient.onOTAMessage(otaMessage);
        }
    }
}
