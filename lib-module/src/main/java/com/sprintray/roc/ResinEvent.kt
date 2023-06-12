package com.sprintray.roc

class ResinEvent(val message: String, val id: String? = null)
class ResinUpdatedEvent(val isUpdated: Boolean)

annotation class ResinRequest {
    companion object {
        const val RESIN_UPDATE = "resin.update"

        const val RESIN_GET_ALL = "resin.get.all"
        const val RESIN_GET_BY_ID = "resin.get.byId"
        const val RESIN_GET_PROFILES = "resin.get.profiles"
    }
}