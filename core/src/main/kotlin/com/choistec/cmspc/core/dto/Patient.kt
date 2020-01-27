package com.choistec.cmspc.core.dto

data class Patient (
        var patient_id:String? = null,
        var patient_name:String? = null,
        var patient_sex:String? = null,
        var patient_birthday:String? = null,
        var patient_contact:String? = null,
        var patient_adress:String? = null,
        var patient_email:String? = null,
        var blood_type_id:String? = null,
        var weight_data:String? = null,
        var height_data:String? = null,
        var medical_department_id:String? = null,
        var ward:String? = null,
        var room:String? = null,
        var medical_staff_id:String? = null,
        var reg_date:String? = null,
        var mod_date:String? = null,
        var discharge_date:String? = null,
        var device_id:String? = null,
        var medical_record:String? = null,
        var del_yn:String? = null
)
