package xyz.lazyghosty.phant0m.api

import android.os.Parcelable
import frb.phant0m.server.ServerInfo
import frb.phant0m.shared.Phant0mApiConstant
import kotlinx.parcelize.Parcelize

@Parcelize
data class Phant0mInfo(
    val serverInfo: ServerInfo = ServerInfo()
) : Parcelable {

    fun getVersionCode(): Long {
        return serverInfo.versionCode
    }

    fun isRunning(): Boolean {
        return Phant0m.pingBinder() && Phant0mApiConstant.server.VERSION_CODE <= getVersionCode()
    }

    fun isNeedUpdate(): Boolean {
        return Phant0mApiConstant.server.VERSION_CODE > getVersionCode() && Phant0m.pingBinder()
    }

    fun isNeedExtraStep(): Boolean {
        return isRunning() && !serverInfo.permission
    }

    fun isRoot(): Boolean {
        return serverInfo.uid == 0
    }

}