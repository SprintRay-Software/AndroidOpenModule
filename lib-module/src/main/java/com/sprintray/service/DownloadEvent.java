package com.sprintray.service;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lvi
 */
@IntDef({

        DownloadEvent.EVENT_DOWNLOADING,
        DownloadEvent.EVENT_DOWNLOAD_SUCCESS,
        DownloadEvent.EVENT_DOWNLOAD_FAILED,
        DownloadEvent.EVENT_INSTALLING,
        DownloadEvent.EVENT_INSTALL_FAILED,
        DownloadEvent.EVENT_INSTALL_SUCCESS,
        DownloadEvent.EVENT_REQUEST_FAILED
})
@Retention(RetentionPolicy.SOURCE)
public @interface DownloadEvent {

    int EVENT_DOWNLOADING = 0;
    int EVENT_DOWNLOAD_SUCCESS = 1;
    int EVENT_DOWNLOAD_FAILED = 2;
    int EVENT_REQUEST_FAILED = 3;

    int EVENT_INSTALLING = 10;
    int EVENT_INSTALL_SUCCESS = 11;
    int EVENT_INSTALL_FAILED = 12;
}

