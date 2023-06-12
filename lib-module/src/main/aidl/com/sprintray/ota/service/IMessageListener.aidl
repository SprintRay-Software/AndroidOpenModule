// IMessageListener.aidl
package com.sprintray.ota.service;
import  com.sprintray.ota.service.OTAMessage;

// Declare any non-default types here with import statements

interface IMessageListener {

   void onOTAMessage(in OTAMessage otaMessae);



}