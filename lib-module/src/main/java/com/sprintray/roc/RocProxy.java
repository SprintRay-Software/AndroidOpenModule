package com.sprintray.roc;

import java.util.List;

public interface RocProxy {

    void updateResin();

    /* Notify the client when there is resin update */

    /* Request to get all resin */
    List<String> getAllResin();

    List<ResinModel> getResinProfiles();

    /* Request to get resin by ID */
    ResinProfileLayerModel getResinById(String id);

}
