package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.define.BaseDefine
import com.choistec.cmspc.core.dto.*
import com.choistec.cmspc.core.mysql.dao.RearerDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.sql.Date

@RestController
@RequestMapping("/rearers")
class RearerController {
    @Autowired
    lateinit var rearerDAO : RearerDAO

    /**
     * 보호자 정보 등록
     */
    @PostMapping("")
    fun insert (@RequestBody requestBody:Map<String,Any>) =
            Rearer(null).apply {
                name = requestBody[NAME].toString()
                contact = requestBody[CONTACT].toString()
                birthday = Date(BaseDefine.BaseDateFormat.parse(requestBody[BIRTHDAY].toString()).time)
            }.apply {
                rearerDAO.insert(requestBody[ADMIN_ID].toString(), this)
            }.id

    /**
     * 보호자 정보 호출
     */
    @GetMapping("")
    fun select (
            @RequestParam admin_id:String?,
            @RequestParam id:Int?,
            @RequestParam contact:String?,
            @RequestParam birthday:String?
    ) = when{
        admin_id!=null&&id!=null-> rearerDAO.selectOne(admin_id,id)
        admin_id!=null-> rearerDAO.select(admin_id)
        contact!=null&&birthday!=null->rearerDAO.selectOneWithValidate(contact,birthday)
        else->null
    }

    /**
     * 보호자 정보 삭제
     */
    @DeleteMapping("")
    fun delete (@RequestParam id:Int) = rearerDAO.delete(id)



}
