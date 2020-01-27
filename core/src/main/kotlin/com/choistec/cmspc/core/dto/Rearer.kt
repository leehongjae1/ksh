package com.choistec.cmspc.core.dto

import java.sql.Date

data class Rearer (
        var id: Int?,
        var name:String?=null,
        var contact:String?=null,
        var birthday:Date?=null
)
