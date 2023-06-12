package com.sprintray.net.utils;

import androidx.annotation.StringDef;

public class HttpSpKeys {

    @StringDef({
            SPKey.SP_ASSESS_TOKEN,
            SPKey.SP_ID_TOKEN,
            SPKey.SP_REFRESH_TOKEN,
            SPKey.SP_NAME_ACCOUNT,
            SPKey.SP_ACCOUNT_EMAIL,
            SPKey.SP_ACCOUNT_PERMISSION
    })
    public @interface SPKey{
        String SP_NAME_ACCOUNT = "SP_NAME_ACCOUNT";
        String SP_ASSESS_TOKEN = "SP_ASSESS_TOKEN";
        String SP_ID_TOKEN = "SP_ID_TOKEN";
        String SP_REFRESH_TOKEN = "SP_REFRESH_TOKEN";
        String SP_ACCOUNT_EMAIL = "SP_ACCOUNT_EMAIL";
        String SP_ACCOUNT_PERMISSION = "SP_ACCOUNT_PERMISSION";
    }


}
