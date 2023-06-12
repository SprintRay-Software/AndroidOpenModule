package com.sprintray.ota.service

data class OTARequest(
    var appKey: String,
    var appVersion: String,

    var osKey: String,
    var osVersion: String,

    var authToken: String,
    var requestTAG: String,
)