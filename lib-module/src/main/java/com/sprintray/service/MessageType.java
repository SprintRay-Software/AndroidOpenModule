package com.sprintray.service;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({

        MessageType.MESSAGE_VERSION_RESPONSE,
        MessageType.MESSAGE_SUCCESS,
        MessageType.MESSAGE_GET_STATE,
        MessageType.MESSAGE_ERROR,
        MessageType.MESSAGE_PROGRESS,
        MessageType.MESSAGE_NOTICE,


})
@Retention(RetentionPolicy.SOURCE)
public @interface MessageType {
    int MESSAGE_VERSION_RESPONSE = 0;
    int MESSAGE_GET_STATE = 1;
    int MESSAGE_SUCCESS = 2;
    int MESSAGE_PROGRESS = 3;
    int MESSAGE_ERROR = 4;
    int MESSAGE_NOTICE = 5;


}