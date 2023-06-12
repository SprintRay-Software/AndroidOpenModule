package com.sprintray.service;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({

        OTAState.STATE_ERROR,
        OTAState.STATE_INIT,
        OTAState.STATE_ON_REQUEST,


        OTAState.STATE_DOWNLOADING,
        OTAState.STATE_READY_INSTALL,
        OTAState.STATE_INSTALLING,
        OTAState.STATE_UPDATING,

        OTAState.EVENT_DOWNLOADING,
        OTAState.EVENT_DOWNLOAD_SUCCESS,
        OTAState.EVENT_DOWNLOAD_FAILED,

        OTAState.EVENT_INSTALL_FAILED,
        OTAState.EVENT_INSTALL_SUCCESS,
        OTAState.EVENT_REQUEST_FAILED,
        OTAState.EVENT_REQUEST_SUCCESS,
        OTAState.EVENT_MAIN_APP_STATE,
        OTAState.EVENT_START_FORCE_SILENT,

})
@Retention(RetentionPolicy.SOURCE)
public @interface OTAState {
    int STATE_ERROR = -1;

    int STATE_INIT = 0;

    //
    int STATE_ON_REQUEST = 101;




    int STATE_DOWNLOADING = 201;



    int STATE_READY_INSTALL = 301;

    int STATE_INSTALLING = 302;

    int STATE_UPDATING = 400;


    int EVENT_DOWNLOADING = 10;
    int EVENT_DOWNLOAD_SUCCESS = 11;
    int EVENT_DOWNLOAD_FAILED = 12;
    int EVENT_REQUEST_FAILED = 13;
    int EVENT_REQUEST_SUCCESS = 14;


    int EVENT_INSTALL_SUCCESS = 21;
    int EVENT_INSTALL_FAILED = 22;

    int EVENT_MAIN_APP_STATE = 23;
    int EVENT_START_FORCE_SILENT = 1001;


}