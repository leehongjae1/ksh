- # API 코드 변경 법
1. com.choistec.cmspc.core.mysql.dao 로 가서 데이터 베이스 구문을
확인한다.
2. 만약 데이터 받을 때 특정 형식으로 바꾸고 싶다면
com.choistec.cmspc.core.mysql.dto 로 가서 멤버 클래스를 변경한다.
3. com.choistec.cmspc.api.controller 로 가서 컨트롤러를 변경한다.
 

 
# 쿼리문


## delete 유저 삭제

~~~
DAO

@Update("""
UPDATE $테이블명$
set $변경할컬럼$ = 1,
$수정날짜컬럼$ = $NOW
WHERE %아이디% = #{$ID}
""")
fun delete(@Param(ID) id:String)
~~~

~~~
Controller

@DeleteMapping("")
fun delete (@RequestParam id:String) = adminDAO.delete(id)
~~~

## SELECT 특정 유저 조회

~~~
DAO

@Select("""
SELECT	
$컬럼내용$
FROM $테이블명$
WHERE
$아이디컬럼$ = #{$아이디변수$} and
$삭제컬럼$ != 1
""")
fun seleteMainBabyInfo(@Param($아이디변수$) $아이디변수$: String): List<MainBabyInfo>
또는
fun seleteMainBabyInfo(@Param($아이디변수$) $아이디변수$: String): List<Map<String,Any?>>
~~~

~~~
Controller
	
@GetMapping("/{$ID}/$경로명$")
fun $함수이름$ (@PathVariable id:Int) = babyDAO.selectRearerId(id)	
~~~

## INSERT 넣기

~~~
DAO

@Insert("""
INSERT INTO $테이블명$
(
admin_id,
baby_position_number,
baby_name,
baby_sex,
baby_birthday,
rearer_name,
rearer_contact,
rearer_birthday,
reg_date,
mod_date,
ward_name,
device_id,
del_yn)
VALUES (
#{baby.admin_id},
#{baby.baby_position_number},
#{baby.baby_name},
#{baby.baby_sex},
#{baby.baby_birthday},
#{baby.rearer_name},
#{baby.rearer_contact},
#{baby.rearer_birthday},
$NOW,
$NOW,
#{baby.ward_name},
#{baby.device_id},
'0'
)
""")
@Options(useGeneratedKeys = true, keyProperty = "baby.baby_id")
fun insertBabyInfo(@Param(baby) baby: $멤버변수명$): Int
~~~

~~~
Controller
	
    @PostMapping("/add/babyinfo")
    fun insertBabyInfo(@RequestBody requestBody:Map<String,Any>) : Int {
        Baby(null).apply {
            admin_id = requestBody["admin_id"].toString()
            baby_position_number = requestBody["baby_position_number"].toString().toInt()
            baby_name = requestBody["baby_name"].toString()
            baby_sex = requestBody["baby_sex"].toString().toInt()
            baby_birthday = requestBody["baby_birthday"].toString()
            rearer_name = requestBody["rearer_name"].toString()
            rearer_contact = requestBody["rearer_contact"].toString()
            rearer_birthday = requestBody["rearer_birthday"].toString()
            reg_date = requestBody["reg_date"].toString()
            mod_date = requestBody["mod_date"].toString()
            ward_name = requestBody["ward_name"].toString()
            device_id = requestBody["device_id"].toString().toInt()
            del_yn = requestBody["del_yn"].toString().toInt()            
            return babyDAO.insertBabyInfo(this)
        }
        return 0
    }
~~~

## UPDATE 유저 정보

~~~

DAO

@Update("""
UPDATE $테이블명$ set
baby_name=#{baby.baby_name},
baby_sex=#{baby.baby_sex},
baby_birthday=#{baby.baby_birthday},
baby_position_number=#{baby.baby_position_number},
rearer_name=#{baby.rearer_name},
rearer_contact=#{baby.rearer_contact},
rearer_birthday=#{baby.rearer_birthday},
mod_date=utc_timestamp(),
ward_name=#{baby.ward_name},
device_id=#{baby.device_id}
WHERE baby_id = #{baby.baby_id}
""")
@Options(useGeneratedKeys = true, keyProperty = "$BABY.$BABY_ID")
fun updateBabyInfo(@Param(BABY) baby: Baby):Int

~~~

~~~

Controller

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
        return babyDAO.updateBabyInfo(this)
    }
    return 0
}
~~~
~~~
DATA CLASS

package com.choistec.smartcradle.core.dto

data class Baby (
        var admin_id:String? = null,
        var baby_position_number:Int? = null,
        var baby_id:Int? = null,
        var baby_name:String? = null,
        var baby_sex:Int? = null,
        var baby_birthday:String?   = null,
        var rearer_name:String? = null,
        var rearer_contact:String? = null,
        var rearer_birthday:String?   = null,
        var reg_date:String?   = null,
        var mod_date:String?   = null,
        var ward_name:String?   = null,
        var cam_id:Int? = null,
        var device_id:Int? = null,
        var del_yn:Int? = null
)

~~~

## 디바이스 체크

~~~
DAO

@Select("""
	select
     baby.admin_id,
	 baby.baby_id,
     baby.baby_name,
     device.device_id
     from $테이블명1$ as baby
     left join $테이블명2$ as device on
     device.device_id = baby.device_id
     where
     device.device_id like #{$DID} and
     baby.del_yn like '0'
""")
fun deviceCheck(@Param(DID) device_id: String?): List<Map<String, Any>>
~~~
~~~
Controller

val device = measureDAO.deviceCheck(it?.id.toString())
if (device.firstOrNull()?.get("admin_id")?.toString() == null) {
} else {
}
~~~

## 최근 정보 기록

~~~
DAO

@Insert("""
INSERT INTO $테이블명$ SET
$DID=#{$DID},
${'$'}{bytes}
ON DUPLICATE KEY UPDATE
${'$'}{bytes}
""")
fun insertRealtimeData(@Param(DID) did: Int, @Param("bytes") value: String)

~~~
~~~
Controller

measureDAO.insertRealtimeData(key, value.toRealTimeQuery())
~~~
~~~
Mesasure

fun toRealTimeQuery(): String = StringBuilder("").apply {
    /**
     * 실시간 측정 데이터 중에
     * 받은 데이터가 있으면
     * 쿼리문에 추가해서 값을
     * 넣어준다.
     */
    if(temp_data!=null) {
        append("${TEMP}_$DATA=$temp_data,")
        append("${TEMP}_$BATTERY=$temp_battery,")
        append("${TEMP}_$MOD_DATE=$NOW,")
    }
    if(heart_data!=null){
        append("${HEART}_$DATA=$heart_data,")
        append("${HEART}_$BATTERY=$heart_battery,")
        append("${HEART}_$MOD_DATE=$NOW,")
    }
    if(breath_data!=null) {
        append("${BREATH}_$DATA=$breath_data,")
        append("${BREATH}_$BATTERY=$breath_battery,")
        append("${BREATH}_$MOD_DATE=$NOW,")
    }
    if(spo2_data!=null){
        append("${SPO2}_$DATA=$spo2_data,")
        append("${SPO2}_$BATTERY=$spo2_battery,")
        append("${SPO2}_$MOD_DATE=$NOW,")
    }
    if(weight_data!=null){
        append("${WEIGHT}_$DATA=$weight_data,")
        append("${WEIGHT}_$BATTERY=$weight_battery,")
        append("${WEIGHT}_$MOD_DATE=$NOW,")
    }
}.dropLast(1).toString()
~~~

# INSERT 그래프 데이터

~~~
DAO

@Insert("""
INSERT INTO AUTO_${'$'}{$BABY_ID}_MEASURE SET
${'$'}{bytes}
,$REG_DATE=$NOW
""")
fun insertData(@Param(BABY_ID) baby_id: String?, @Param("bytes") value: String)

~~~

~~~
Controller

measureDAO.insertData(value.baby_id, value.toQuery())
~~~

~~~
Measure

fun toQuery(): String = StringBuilder("").apply {
    temp_data?.let { append("$TEMP=$it,") }
    heart_data?.let { append("$HEART=$it,") }
    breath_data?.let { append("$BREATH=$it,") }
    spo2_data?.let { append("$SPO2=$it,") }
    weight_data?.let { append("$WEIGHT=$it,") }
}.dropLast(1).toString()
~~~