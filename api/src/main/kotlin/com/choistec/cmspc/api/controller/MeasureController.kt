package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.dto.ADMIN_ID
import com.choistec.cmspc.core.dto.BABY
import com.choistec.cmspc.core.dto.DID
import com.choistec.cmspc.core.dto._ID
import com.choistec.cmspc.core.mysql.dao.MeasureDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/devices")
class MeasureController {
    @Autowired
    lateinit var measureDAO: MeasureDAO

    @GetMapping("/data/environment")
    fun seleteEnvironmentLast(@RequestParam hostpital_id:String) = measureDAO.seleteEnvironmentLast(hostpital_id)

    @GetMapping("/info")
        fun seleteDeviceInfo(@RequestParam admin_id:String) = measureDAO.seleteDeviceInfo(admin_id)

    /**
     * 아기 아이디로 장비 정보 호출하기
     */
    @GetMapping("")
    fun select (
            //관리자 아이디
            @RequestParam admin_id:String?,
            //아기 아이디
            @RequestParam baby_id:Int?
    ) = baby_id?.let {
        measureDAO.selectDevices(it)
    }?:admin_id?.let {
        measureDAO.selectDevicesWhereAdmin(it)
    }

    /**
     * 카메라 정보 불러오기
     */
    @GetMapping("/{$BABY$_ID}/camera")
    fun selectCamera (
            @PathVariable baby_id:Int
    ) = measureDAO.selectCamera(baby_id).firstOrNull()

    /**
     * 아기 아이디에 장비 등록
     */
    @PutMapping("/{$BABY$_ID}")
    fun regist (
            @PathVariable baby_id:Int,
            @RequestBody body:Map<String,String?>
    ) = body[ADMIN_ID]?.let { adminId->
        body[DID]?.let { did ->
            measureDAO.unregister(baby_id, adminId)
            measureDAO.register(baby_id, adminId, did.toInt())
        }?:let {
            measureDAO.unregister(baby_id, adminId)
        }
    }
}
