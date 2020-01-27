package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.dto.Staff
import com.choistec.cmspc.core.mysql.dao.StaffDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/staff")
class StaffController {
    /**
     * 의료진 추가
     */
    @Autowired
    lateinit var staffDAO : StaffDAO
    @PostMapping("/add")
    fun insertStaff(@RequestBody requestBody:Map<String,Any>) : Int {
        Staff(null).apply {
            medical_staff_id = requestBody["medical_staff_id"].toString()
            password = requestBody["password"].toString()
            medical_staff_name = requestBody["medical_staff_name"].toString()
            medical_staff_contact = requestBody["medical_staff_contact"].toString()
            medical_staff_email = requestBody["medical_staff_email"].toString()
            group_priority = requestBody["group_priority"].toString()
            position_info_id = requestBody["position_info_id"].toString()
            medical_department_id = requestBody["medical_department_id"].toString()
            ward_id = requestBody["ward_id"].toString()
            reg_date = requestBody["reg_date"].toString()
            mod_date = requestBody["mod_date"].toString()
            del_yn = requestBody["del_yn"].toString()
            return staffDAO.insertStaffInfo(this)
        }
        return 0
    }

}
