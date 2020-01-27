package com.choistec.cmspc.core.dto

data class Staff (
        var medical_staff_id:String? = null,
        var password:String? = null,
        var medical_staff_name:String? = null,
        var medical_staff_contact:String? = null,
        var medical_staff_email:String? = null,
        var group_priority:String? = null,
        var position_info_id:String? = null,
        var medical_department_id:String? = null,
        var ward_id:String? = null,
        var reg_date:String? = null,
        var mod_date:String? = null,
        var del_yn:String? = null
)
