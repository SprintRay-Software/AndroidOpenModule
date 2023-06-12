package com.sprintray.service;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author lvi
 */
@StringDef({
        UpdateEvent.ACTION_CHECK_UPDATE,
        UpdateEvent.ACTION_CHECK_UPDATE_RESPONSE,
        UpdateEvent.ACTION_DOWNLOAD,
        UpdateEvent.ACTION_DOWNLOAD_PROGRESS,
        UpdateEvent.ACTION_DOWNLOAD_SUCCESS,
        UpdateEvent.ACTION_DOWNLOAD_FAILED,
        UpdateEvent.ACTION_CHECK_FAILED,
        UpdateEvent.ACTION_CHECK_STATE,
        UpdateEvent.ACTION_CHECK_STATE_RESULT,

        UpdateEvent.ACTION_INSTALL,
        UpdateEvent.ACTION_INSTALL_SUCCESS,
        UpdateEvent.ACTION_INSTALL_FAILED,
        UpdateEvent.ACTION_INSTALL_PROGRESS,
        UpdateEvent.ACTION_STOP,
        UpdateEvent.ACTION_SEND_MAN_STATE,
        UpdateEvent.ACTION_NOTICE,
})
@Retention(RetentionPolicy.SOURCE)
public @interface UpdateEvent {


    /**
     *
     */
    String ACTION_CHECK_STATE = "update.action.check.state";
    String ACTION_CHECK_STATE_RESULT = "update.action.check.state.result";

    String ACTION_SEND_MAN_STATE = "update.action.send.man.state";


    /**
     *
     */
    String ACTION_CHECK_UPDATE = "update.action.check.update";
    String ACTION_CHECK_UPDATE_RESPONSE = "update.action.check.update.response";
    String ACTION_CHECK_FAILED = "update.action.check.failed";

    String ACTION_DOWNLOAD = "update.action.download";
    String ACTION_DOWNLOAD_PROGRESS = "update.action.download.progress";
    String ACTION_DOWNLOAD_SUCCESS = "update.action.download.success";
    String ACTION_DOWNLOAD_FAILED = "update.action.download.failed";


    String ACTION_INSTALL = "update.action.install";
    String ACTION_INSTALL_SUCCESS = "update.action.install.success";
    String ACTION_INSTALL_FAILED = "update.action.install.failed";
    String ACTION_INSTALL_PROGRESS = "update.action.install.progress";


    String ACTION_STOP = "update.action.stop";


    String ACTION_NOTICE = "update.action.notice";


}
