// IRoCInterface.aidl
package com.sprintray.roc;
import  com.sprintray.roc.IRocListener;

// Declare any non-default types here with import statements
parcelable ResinModel;
parcelable ResinProfileLayerModel;

interface IRoCInterface {
   /* Request to update resin data from the client */
       void updateResin();

       void onResinState(String state);

       /* Request to get all resin */
       List<String> getAllResin();

       List<ResinModel> getResinProfiles();

       /* Request to get resin by ID */
       ResinProfileLayerModel getResinById(String id);

       void registerReceiveListener(IRocListener listener);

       void unregisterReceiveListener(IRocListener listener);
}