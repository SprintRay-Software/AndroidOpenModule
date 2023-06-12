package com.sprintray.service;

import android.os.Parcel;
import android.os.Parcelable;

public class OTAMessage implements Parcelable {

    private int progress;
    private long current;
    private long total;
    private String appName;
    private int otaState;
    private int messageType;
    private String message;
    private String updateResponse;

    /**
     * *

     * @param message
     * @param state
     * @param messageType
     */

    public OTAMessage(String message, int state, int messageType,String updateResponse) {
        this.appName = "";
        this.progress = 0;
        this.current = 0;
        this.total = 0;
        this.otaState = state;
        this.message = message;
        this.messageType =messageType;
        this.updateResponse = updateResponse;
    }


    public OTAMessage(String appName, String message, int state, int messageType,String updateResponse) {
        this.appName = appName;
        this.progress = 0;
        this.current = 0;
        this.total = 0;
        this.otaState = state;
        this.message = message;
        this.messageType = messageType;
        this.updateResponse =updateResponse;
    }


    public OTAMessage(String appName, String message, int progress, long current, long total, int state,int messageType,String updateResponse) {
        this.appName = appName;
        this.progress = progress;
        this.current = current;
        this.total = total;
        this.otaState = state;
        this.message = message;
        this.messageType = messageType;
        this.updateResponse = updateResponse;
    }


    protected OTAMessage(Parcel in) {
        appName = in.readString();
        message = in.readString();
        otaState = in.readInt();
        messageType = in.readInt();

        progress = in.readInt();
        current = in.readLong();
        total = in.readLong();
        updateResponse = in.readString();
    }

    public static final Creator<OTAMessage> CREATOR = new Creator<OTAMessage>() {
        @Override
        public OTAMessage createFromParcel(Parcel in) {
            return new OTAMessage(in);
        }

        @Override
        public OTAMessage[] newArray(int size) {
            return new OTAMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(appName);
        parcel.writeString(message);

        parcel.writeInt(otaState);
        parcel.writeInt(messageType);

        parcel.writeInt(progress);
        parcel.writeLong(current);
        parcel.writeLong(total);

        parcel.writeString( updateResponse);
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getOtaState() {
        return otaState;
    }

    public void setOtaState(int otaState) {
        this.otaState = otaState;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }


    public String getUpdateResponse() {
        return updateResponse;
    }

    public void setUpdateResponse(String updateResponse) {
        this.updateResponse = updateResponse;
    }


    @Override
    public String toString() {
        return "OTAMessage{" +
                "progress=" + progress +
                ", current=" + current +
                ", total=" + total +
                ", appName='" + appName + '\'' +
                ", otaState=" + otaState +
                ", messageType=" + messageType +
                ", message='" + message + '\'' +
                ", updateResponse='" + updateResponse + '\'' +
                '}';
    }
}
