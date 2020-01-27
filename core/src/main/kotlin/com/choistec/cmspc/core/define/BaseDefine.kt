package com.choistec.cmspc.core.define

import java.text.SimpleDateFormat

object BaseDefine{
    const val LOCAL_URL = "localhost"
    const val LOCAL_MQTT_URL = "localhost"
    const val AMAZON_URL = "cms.choistec.com"
    enum class LogMode {Debug,Default,Mute}

    val BaseDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
}
