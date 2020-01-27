package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.dto.*
import com.choistec.cmspc.core.mysql.dao.GrowthDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp

@RestController
@RequestMapping("/growths")
class GrowthController {
    @Autowired
    lateinit var growthDAO: GrowthDAO

    /**
     * 최근 발육 정보 기록 데이터 호출
     */
    @GetMapping("")
    fun select (
            @RequestParam admin_id:String?,
            @RequestParam baby_id:Int?,
            @RequestParam offset:Float
    ) = baby_id?.let {
        growthDAO.selectOne(it,offset)
    }?:admin_id?.let {
        print("발육 정보 불러옴")
        growthDAO.selectWhereAdmin(it,offset)
    }


    @GetMapping("/data/last")
    fun seleteGrowthLastData (@RequestParam admin_id:String) = growthDAO.seleteGrowthLastData(admin_id)

    @GetMapping("/graph/day")
    fun seleteGraphDay(@RequestParam baby_id:String) = growthDAO.seleteGraphDay(baby_id)

    @GetMapping("/graph/time")
    fun seleteGraphTime(@RequestParam baby_id:String, @RequestParam search_date:String) = growthDAO.seleteGraphTime(baby_id,search_date)


    /**
     * 발육정보 기록 추가
     */
    @PostMapping("/add")
    fun insertGrowthInfo(@RequestBody requestBody:Map<String,Any>
    ) = GrowthInfo(null).apply {
        admin_id = requestBody["admin_id"].toString()
        baby_id = requestBody["baby_id"].toString()
        nurse = requestBody["nurse"].toString()
        nurse_mod_date = requestBody["nurse_mod_date"].toString()
        feces = requestBody["feces"].toString()
        feces_mod_date = requestBody["feces_mod_date"].toString()
        urine = requestBody["urine"].toString()
        urine_mod_date = requestBody["urine_mod_date"].toString()
        vomit = requestBody["vomit"].toString()
        vomit_mod_date = requestBody["vomit_mod_date"].toString()
        growthDAO.insertGrowthInfoGraph(admin_id,baby_id, this.toRealTimeQuery())
    }


    /**
     * 발육정보 기록 추가
     */
    @PostMapping("")
    fun insert (@RequestBody requestBody:Map<String,Any>
    ) = Baby.Growth(null).apply {
        admin_id = requestBody["admin_id"].toString()
        baby_id = requestBody["baby_id"].toString()
        nurse = requestBody["nurse"].toString()
        nurse_mod_date = requestBody["nurse_mod_date"].toString()
        feces = requestBody["feces"].toString()
        feces_mod_date = requestBody["feces_mod_date"].toString()
        urine = requestBody["urine"].toString()
        urine_mod_date = requestBody["urine_mod_date"].toString()
        vomit = requestBody["vomit"].toString()
        vomit_mod_date = requestBody["vomit_mod_date"].toString()
        growthDAO.insert(this)

    }

            //growthDAO.insert(Baby.Growth(baby_id, nurse, feces, urine, vomit))

    /**
     * 그래프
     */
    @GetMapping("/{$ID}/$DATA")
    fun graph (
            @PathVariable id:Int,
            @RequestParam offset:Float
    ) = growthDAO.selectAllData(id,offset)
            .map {
                (it[REG_DATE] as? Timestamp)?.time to arrayOf("nurse","feces","urine","vomit").map { key ->
                    it[key]
                }
            }.toMap()
}
