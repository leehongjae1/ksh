package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.dto.ID
import com.choistec.cmspc.core.mysql.dao.HospitalDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hospital")
class HospitalInfoController {

    @Autowired
    lateinit var hospitalDAO: HospitalDAO
    /**
     * 모든 진료과 정보 조회
     */
    @GetMapping("/info/department")
    fun selectMedicalDepartment() = hospitalDAO.selectMedicalDepartment()

    /**
     * 진료과 병동 조회
     * $ID 진료과 아이디
     */
    @GetMapping("{$ID}/ward")
    fun selectWard (
            @PathVariable id:Int
    ) = hospitalDAO.selectWard(id)


    @GetMapping("/{medical_department_id}/{ward_id}/room")
    fun selectRoom(
            @PathVariable medical_department_id:Int,
            @PathVariable ward_id:Int
    ) = hospitalDAO.selectRoom(medical_department_id,ward_id)

    /**
     * 모든 직책 정보 조회
     */
    @GetMapping("/position")
    fun selectMedicalPosition() = hospitalDAO.selectMedicalPosition()

    @GetMapping("/blood")
    fun selectBloodType() = hospitalDAO.selectBloodType()





}