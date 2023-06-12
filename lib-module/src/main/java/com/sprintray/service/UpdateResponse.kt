package com.sprintray.ota.service

import com.blankj.utilcode.util.Utils
import com.google.gson.annotations.SerializedName



public class UpdateResponse {


    enum class UpdateMethod(val type: Int) {
        /**
         *  it means that the local version number is the same as the server version number
         */
        UPDATE_NONE(0),
        /**
         *  it means that the local version number is small than the server version number
         */
        UPDATE_UPGRADE(1),
        /**
         *  it means that the local version number is greater than the server version number
         */
        UPDATE_DOWNGRADE(2),
    }





    enum class UpdateAction(val type: Int) {

        NONE(0),

        UPGRADE_NORMAL(1),

        DOWNGRADE_NORMAL(2),

        UPGRADE_SILENT(3),

        DOWNGRADE_SILENT(4),
        //
        UPGRADE_DOWNGRADE_SILENT(3),


    }






    @SerializedName("PrinterId")
    var printerId: String? = null

    @SerializedName("ReleaseNotes")
    var releaseNotes: String? = null

    @SerializedName("VersionNumber")
    var versionNumber: String? = null

    @SerializedName("ForceUpdate")
    var forceUpdate: Boolean? = null

    @SerializedName("SilentUpdate")
    var silentUpdate: Boolean? = null

    @SerializedName("FirmwareVersionNumber")
    var firmwareVersionNumber: String? = null

    @SerializedName("Dependencies")
    var dependencies: DependenciesDTO? = null



    var requestTAG :String = "";


    var osVersionNumber: String? = null
    var appVersionNumber: String? = null



    var softwareUpdateMethod = UpdateMethod.UPDATE_NONE;
    var firmwareUpdateMethod = UpdateMethod.UPDATE_NONE;

    var softwareUpdateAction = UpdateAction.NONE;
    var firmwareUpdateAction  = UpdateAction.NONE;



    var softwareSilent = false;
    var firmwareSilent = false;



    var appKey: String? = null
    var osKey: String? = null


    var softwareFileSize: Long = 0
    var firmwareFileSize: Long = 0

    var softwareFilePath: String? = null
    var firmwareFilePath: String? = null

    var isSoftwareDownloadSuccess = false
    var isFirmwareDownloadSuccess = false

    var isSoftwareInstallSuccess = false
    var isFirmwareInstallSuccess = false








    class DependenciesDTO {
        @SerializedName("capricorn-core")
        var capricorncore: CapricorncoreDTO? = null

        @SerializedName("ota-module")
        var otamodule: OtamoduleDTO? = null

        @SerializedName("security-module")
        var securitymodule: SecuritymoduleDTO? = null

        @SerializedName("android-os")
        var androidOS: AndroidOSDTO? = null

        @SerializedName("apk-module")
        var apkModule: ApkModuleDTO? = null

        class CapricorncoreDTO {
            @SerializedName("version")
            var version: String? = null

            @SerializedName("s3Key")
            var s3Key: String? = null

            @SerializedName("md5")
            var md5: String? = null
        }

        class OtamoduleDTO {
            @SerializedName("version")
            var version: String? = null

            @SerializedName("s3Key")
            var s3Key: String? = null

            @SerializedName("md5")
            var md5: String? = null
        }

        class SecuritymoduleDTO {
            @SerializedName("version")
            var version: String? = null

            @SerializedName("s3Key")
            var s3Key: String? = null

            @SerializedName("md5")
            var md5: String? = null
        }

        class AndroidOSDTO {
            @SerializedName("version")
            var version: String? = null

            @SerializedName("s3Key")
            var s3Key: String? = null

            @SerializedName("md5")
            var md5: String? = null
        }

        class ApkModuleDTO {
            @SerializedName("version")
            var version: String? = null

            @SerializedName("s3Key")
            var s3Key: String? = null

            @SerializedName("md5")
            var md5: String? = null
        }
    }
}