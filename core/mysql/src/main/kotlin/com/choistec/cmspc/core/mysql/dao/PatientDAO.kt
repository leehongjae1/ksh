package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface PatientDAO {

    @Select("""
    SELECT
    info.baby_position_number,
    info.baby_id,
    info.rearer_name,
    info.baby_name,
    info.ward_name,
    info.baby_sex,
    growth.nurse,
    DATE_ADD(growth.nurse_mod_date, INTERVAL (-540)*(-1) minute) as nurse_mod_date,
    if(timestampdiff(second , measure.heart_mod_date, utc_timestamp()) > 120, null, round(measure.heart_data)) as heart,
    if(timestampdiff(second , measure.breath_mod_date, utc_timestamp()) > 120, null, round(measure.breath_data)) as breath,
    if(timestampdiff(second , measure.spo2_mod_date, utc_timestamp()) > 120, null, round(measure.spo2_data)) as spo2,
    if(timestampdiff(second , measure.temp_mod_date, utc_timestamp()) > 120, null, round(measure.temp_data,1)) as temp,
    if(timestampdiff(second , measure.weight_mod_date, utc_timestamp()) > 120, null, round(measure.weight_data,2)) as weight,
    DATE_FORMAT(DATE_ADD(info.reg_date, INTERVAL (-9)*(-1) hour),'%Y-%m-%d') as reg_date
    FROM baby_info as info
    LEFT JOIN measure_last as measure
    ON measure.did = info.device_id
    LEFT JOIN baby_growth_last AS growth
    ON info.baby_id = growth.baby_id
    WHERE
    info.del_YN LIKE '0' and
    info.admin_id = #{$ADMIN_ID}
    group by info.baby_id
	""")
    fun seleteMainBabyInfo(@Param(ADMIN_ID) admin_id: String): List<MainBabyInfo>

    @Select("""
    SELECT
    info.admin_id, 
    baby_id, 
    baby_position_number, 
    baby_name, 
    baby_sex, 
    baby_birthday, 
    rearer_name, 
    rearer_contact, 
    rearer_birthday, 
    ward_name,
    DATE_FORMAT(reg_date, '%Y-%m-%d') as reg_date, 
    mod_date, 
    device.cam_id, 
    info.device_id, 
    del_yn
    FROM baby_info as info
    LEFT JOIN map_device AS device
    ON device.device_id = info.device_id
    WHERE
    info.del_YN LIKE '0'and
    info.admin_id = #{$ADMIN_ID}
	""")
    fun seleteSetupBabyInfo(
            @Param(ADMIN_ID) admin_id: String): List<SetupBabyInfo>

    @Select("""
    SELECT 
    id,
    avg(`temp`) AS `temp`,
    avg(`heart`) AS `heart`,
    avg(`breath`) AS `breath`,
    avg(`spo2`) AS `spo2`,
    avg(`weight`) AS `weight`,
    DATE_FORMAT(DATE(DATE_ADD(`reg_date`, INTERVAL (-9)*(-1) hour)), '%Y-%m-%d %T') AS `date`
    FROM AUTO_${'$'}{$BABY_ID}_MEASURE
    GROUP BY `date`;
	""")
    fun seleteGraphDay(
            @Param(BABY_ID) baby_id: String): List<BodySignalGraph>

    @Select("""
    SELECT
    id,
    temp,
    heart,
    breath,
    spo2,
    weight,
    DATE_ADD(reg_date, INTERVAL (-9)*(-1) hour) as `date`
    FROM AUTO_${'$'}{$BABY_ID}_MEASURE
    WHERE
    DATE_FORMAT(DATE_ADD(`reg_date`, INTERVAL (-9)*(-1) hour), '%Y-%m-%d') LIKE #{$SEARCH_DATE}
	""")
    fun seleteGraphTime(
            @Param(BABY_ID) baby_id: String,
            @Param(SEARCH_DATE) search_date: String
    ): List<BodySignalGraph>

    @Select("""
    SELECT	
    *
    FROM protect_info
    WHERE
    patient_id = #{patient_id}
    """)
    fun seleteProtector(@Param("patient_id") patient_id: Int): List<Map<String, Any?>>

    /**
     * 양육자 아이디가 있는지 확인하는 쿼리문
     */
    @Select("""
    SELECT
		$REARER_ID
	FROM $BABY$_USER
	where
		$ID = #{$ID}
	""")
    fun selectRearerId(@Param(ID) id: Int): Int

    @Insert("""
    INSERT INTO baby_info
    (admin_id,
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
    fun insertBabyInfo(@Param(BABY) baby: Baby): Int

    @Insert("""
     INSERT INTO patient_info
     (
     patient_name,
     patient_sex,
     patient_birthday,
     patient_contact,
     patient_adress,
     patient_email,
     blood_type_id,
     weight_data,
     height_data,
     medical_department_id,
     ward,
     room,
     medical_staff_id,
     reg_date,
     mod_date,
     discharge_date,
     device_id,
     medical_record,
     del_yn)
     VALUES
     (
     #{patient.patient_name},
     #{patient.patient_sex},
     #{patient.patient_birthday},
     #{patient.patient_contact},
     #{patient.patient_adress},
     #{patient.patient_email},
     #{patient.blood_type_id},
     #{patient.weight_data},
     #{patient.height_data},
     #{patient.medical_department_id},
     #{patient.ward},
     #{patient.room},
     #{patient.medical_staff_id},
     #{patient.reg_date},
     #{patient.mod_date},
     #{patient.discharge_date},
     #{patient.device_id},
     #{patient.medical_record},
     #{patient.del_yn}
     )
     """)
    @Options(useGeneratedKeys = true, keyProperty = "patient.patient_id")
    fun insertPatientInfo(@Param("patient") patient: Patient): Int

    @Select("""
    SELECT 
    MAX(baby_id) as baby_id
    FROM baby_info;
    """)
    fun selectBabyIdMax(): Map<String, Any?>

    @Update("""
    UPDATE $BABY$_USER set
    $NAME=#{$BABY.$NAME},
    sex = #{$BABY.sex},
    birthday = #{$BABY.birthday},
    $MOD_DATE = $NOW
    WHERE $ID = #{$BABY.$ID}
	""")
    @Options(useGeneratedKeys = true, keyProperty = "$BABY.$ID")
    fun update(@Param(BABY) baby: Baby)

    @Update("""
    UPDATE baby_info set
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
    fun updateBabyInfo(@Param(BABY) baby: Baby): Int

    @Update("""
    UPDATE baby_info
    set $DEL = 1,
    $MOD_DATE = $NOW
    WHERE $BABY_ID = #{$BABY_ID}
    """)
    fun deleteBabyInfo(@Param(BABY_ID) baby_id: Int): Int

    @Update("""
    UPDATE $BABY$_USER
    set $DEL = 0
    WHERE $ID = #{$ID}
    """)
    fun restore(@Param(ID) id: Int)

    @Update("""
    UPDATE MAP_DEVICE
    SET
    	$BABY_ID=null
    WHERE
    	$BABY_ID=#{$BABY_ID}
	""")
    fun unregister(@Param(BABY_ID) baby_id: Int)

    @Select("""
    SELECT
    	$ID,
    	$NAME,
    	sex,
    	birthday
    FROM $BABY$_USER
    WHERE
    	$REARER_ID = #{$REARER_ID} and
    	$DEL != 1
    """)
    fun selectWhereRearer(@Param(REARER_ID) rearer_id: Int): List<Baby>

    /*부모의 컬럼을 확인한다.*/
    @Select("""
    SELECT
    	${'$'}{$COLUMNS}
    FROM $REARER$_USER as $REARER
    LEFT JOIN $BABY$_USER as $BABY
    	ON $REARER.$ID = $REARER$_ID
    WHERE
    	$ADMIN_ID = #{$ADMIN_ID} and
    	$BABY.$DEL != 1
    """)
    fun selectWhereAdmin(
            @Param(ADMIN_ID) admin_id: String,
            @Param(COLUMNS) option: String = """
    $BABY.$ID,
    $BABY.$NAME,
    $BABY.$SEX,
    $BABY.$BIRTHDAY
    """): List<Baby>

    @Select("""
    SELECT
    	$ID,
    	$NAME,
    	$SEX,
    	$BIRTHDAY
    FROM $BABY$_USER
    WHERE
    	$ID = #{$ID} and
    	$DEL != 1
    """)
    fun selectOne(@Param(ID) id: Int): List<Baby>

    /**
     * 생체 신호를 기록하는 테이블
     */
    @Select("""
	CREATE TABLE IF NOT EXISTS AUTO_${'$'}{$ID}_MEASURE (
		$ID BIGINT NOT NULL AUTO_INCREMENT,
		$TEMP float DEFAULT NULL,
  		$HEART float DEFAULT NULL,
  		$BREATH INT unsigned DEFAULT NULL,
  		$SPO2 float DEFAULT NULL,
  		$WEIGHT float DEFAULT NULL,
		$REG_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY ($ID)
	)
	ENGINE=InnoDB DEFAULT CHARSET=utf8
	""")
    fun tableCreate(@Param(ID) id: Int): String

    /**
     * 생체 신호 그래프 테이블을 삭제하는 쿼리문
     */
    @Select("""
	DROP TABLE AUTO_${'$'}{$ID}_MEASURE
	""")
    fun tableDrop(@Param(ID) id: Int)

    @Select("""
    SELECT
    	$BABY$_ID,
    	${TEMP}_$DATA as $TEMP,
    	${HEART}_$DATA as $HEART,
    	${BREATH}_$DATA as $BREATH,
    	${SPO2}_$DATA as $SPO2,
    	${WEIGHT}_$DATA as $WEIGHT
	FROM map_device as mqttMap
 	left join measure_last as last
 		on mqttMap.$DID = last.$DID
 	where
 		$ADMIN_ID = #{$ADMIN_ID} and
 		$BABY_ID is Not null
	""")
    fun selectRealtimeData(@Param(ADMIN_ID) admin_id: String): List<Baby.RealTimeData>

    @Select("""
    SELECT
    	${'$'}{value}
    	DATE_ADD(timestamp($REG_DATE), INTERVAL #{offset} hour)
	FROM AUTO_${'$'}{$ID}_MEASURE
 	where
 		left($REG_DATE,10) = #{$REG_DATE}
 	ORDER BY $ID 
	""")
    fun selectData(
            @Param(ID) id: Int,
            @Param(REG_DATE) reg_date: String,
            @Param("offset") offset: Float,
            @Param("value") value: String = """,
    $TEMP,
    $HEART,
    $BREATH,
    $SPO2,
    """): List<Map<String, Any?>>


    @Select("""
    SELECT
    	${'$'}{value}
    	$REG_DATE
	FROM AUTO_${'$'}{$ID}_MEASURE
 	ORDER BY $ID
	""")
    fun selectAllData(
            @Param(ID) id: Int,
            @Param("offset") offset: Float,
            @Param("value") value: String = """,
    $TEMP,
    $HEART,
    $BREATH,
    $SPO2,
    """): List<Map<String, Any?>>


}
