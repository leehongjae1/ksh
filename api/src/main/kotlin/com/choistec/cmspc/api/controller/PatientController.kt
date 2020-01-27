package com.choistec.cmspc.api.controller

import com.choistec.cmspc.core.dto.*
import com.choistec.cmspc.core.mysql.dao.PatientDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp

@RestController
@RequestMapping("/patient")
class PatientController {

    @Autowired
    lateinit var patientDAO : PatientDAO

    /**
     * 아기 추가
     */

//    @PostMapping("")
//    fun insert (@RequestBody requestBody:Map<String,Any>) =
//            Baby(null).apply {
//                baby_name = requestBody[NAME].toString()
//                baby_sex = requestBody[SEX].toString().toInt()
//                baby_birthday = Date(BaseDefine.BaseDateFormat.parse(requestBody[BIRTHDAY].toString()).time)
//            }.takeIf {
//                babyDAO.insert(
//                        requestBody[REARER_ID].toString().toInt(),
//                        it
//                )==1
//            }?.baby_id?.also {
//                babyDAO.tableCreate(it)
//            }

    /**
     * 환자 추가
     */
    @PostMapping("/add/patientinfo")
    fun insertPatientInfo(@RequestBody requestBody:Map<String,Any>) : Int {
        Patient(null).apply {
            patient_name = requestBody["patient_name"].toString()
            patient_sex = requestBody["patient_sex"].toString()
            patient_birthday = requestBody["patient_birthday"].toString()
            patient_contact = requestBody["patient_contact"].toString()
            patient_adress = requestBody["patient_adress"].toString()
            patient_email = requestBody["patient_email"].toString()
            blood_type_id = requestBody["blood_type_id"].toString()
            weight_data = requestBody["weight_data"].toString()
            height_data = requestBody["height_data"].toString()
            medical_department_id = requestBody["medical_department_id"].toString()
            ward = requestBody["ward"].toString()
            room = requestBody["room"].toString()
            medical_staff_id = requestBody["medical_staff_id"].toString()
            reg_date = requestBody["reg_date"].toString()
            mod_date = requestBody["mod_date"].toString()
            discharge_date = requestBody["discharge_date"].toString()
            device_id = requestBody["device_id"].toString()
            medical_record = requestBody["medical_record"].toString()
            del_yn = requestBody["del_yn"].toString()
            return patientDAO.insertPatientInfo(this)
        }
        return 0
    }



    /**
     * 아기 정보 수정
     */
    @PutMapping("/update/babyinfo")
    fun updateBabyInfo (@RequestBody requestBody:Map<String,Any>) : Int {
        Baby(requestBody[ID].toString()).apply {
            baby_id = requestBody["baby_id"].toString().toInt()
            admin_id = requestBody["admin_id"].toString()
            baby_position_number = requestBody["baby_position_number"].toString().toInt()
            baby_name = requestBody["baby_name"].toString()
            baby_sex = requestBody["baby_sex"].toString().toInt()
            baby_birthday = requestBody["baby_birthday"].toString()
            rearer_name = requestBody["rearer_name"].toString()
            rearer_contact = requestBody["rearer_contact"].toString()
            rearer_birthday = requestBody["rearer_birthday"].toString()
            ward_name = requestBody["ward_name"].toString()
            device_id = requestBody["device_id"].toString().toInt()
        }.apply {
            return patientDAO.updateBabyInfo(this)
        }
        return 0
    }

    /**
     * 아기 정보 수정
     */
    @PutMapping("")
    fun update (@RequestBody requestBody:Map<String,Any>) =
            Baby(requestBody[ID].toString()).apply {
                baby_name = requestBody[NAME].toString()
                baby_sex = requestBody[SEX].toString().toInt()
                baby_birthday = requestBody[BIRTHDAY].toString()
            }.apply {
                patientDAO.update(this)
            }


    @GetMapping("/main/babyinfo")
    fun seleteMainBabyInfo (@RequestParam admin_id:String) = patientDAO.seleteMainBabyInfo(admin_id)

    @GetMapping("/setup/babyinfo")
    fun seleteSetupBabyInfo(@RequestParam admin_id:String) = patientDAO.seleteSetupBabyInfo(admin_id)

    @GetMapping("/graph/day")
    fun seleteGraphDay(@RequestParam baby_id:String) = patientDAO.seleteGraphDay(baby_id)

    @GetMapping("/graph/time")
    fun seleteGraphTime(@RequestParam baby_id:String,
                        @RequestParam search_date:String) = patientDAO.seleteGraphTime(baby_id, search_date)


    /**
     * 아기 정보 호출
     */
    @GetMapping("")
    fun select (
            //관리자 아이디
            @RequestParam admin_id:String?,
            //양육자 아이디
            @RequestParam rearer_id:Int?,
            //아이디
            @RequestParam id:Int?,
            //컬럼
            @RequestParam column:String? = null
    ) = when{
        id!=null-> patientDAO.selectOne(id)
        rearer_id!=null-> patientDAO.selectWhereRearer(rearer_id)
        admin_id!=null->
            if(column == null){ patientDAO.selectWhereAdmin(admin_id)}
            else{ patientDAO.selectWhereAdmin(admin_id,column)}
        else->null
    }

    /**
     * 아기 정보 삭제
     */
    @DeleteMapping("/delete/babyinfo")
    fun deleteBabyInfo(@RequestParam baby_id:Int): Int {
        patientDAO.run {
        return deleteBabyInfo(baby_id)
            //tableDrop(id)
            //unregister(id)
        }
    }



    /**
     * 주변 환경 정보 호출
     */
    @GetMapping("/data")
    fun realTimeData (@RequestParam admin_id:String) = patientDAO.selectRealtimeData(admin_id)


    /**
     * 산모 정보 호출
     */
    @GetMapping("/{$ID}/$REARER")
    fun rearer (@PathVariable id:Int) = patientDAO.selectRearerId(id)


    /**
     * 보호자 정보 조회
     */
    @GetMapping("/{patient_id}/protector")
    fun protector (@PathVariable patient_id:Int) = patientDAO.seleteProtector(patient_id)

    /**
     * 생체 신호 발육 정보 그래프
     */
    @GetMapping("/{$ID}/$DATA")
    fun graph (
            //아이디
            @PathVariable id:Int,
            //등록일
            @RequestParam reg_date:String?,
            //offset : 주소를 만들기 위해 기준이 되는 주소에 더해진 값을 의미
            @RequestParam offset:Float,
            //값
            @RequestParam value:String?
    ) = (reg_date?.let {
        patientDAO.selectData(id, reg_date, offset,value?.plus(",") ?: "")
    }?:let {
        patientDAO.selectAllData(id, offset,value?.plus(",") ?: "")
    }).map {
        ArrayList<Any>().apply {
            add(it[REG_DATE] ?: 0)
            value?.split(",")?.forEach { key ->
                add(it[key] ?: "0")
            }
        }
    }.map {
        (it[0] as? Timestamp)?.time to it.subList(1, it.size)
    }.toMap()
}
