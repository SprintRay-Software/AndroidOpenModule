// IOTAInterface.aidl
package com.sprintray.ota.service;

// Declare any non-default types here with import statements
import  com.sprintray.ota.service.IMessageListener;


interface IOTAInterface {


    void initOTA(String value);

    void registerReceiveListener(IMessageListener receiveListener);

    void unregisterReceiveListener(IMessageListener receiveListener);


    void requestVersionJson(String request);


    void requestDownload(String downloadInfo);




    void install();

    void getState();

    void openOSUpdate();

    void stop();


    void sendMainState(String value);

    void switchEnv(String env);


}