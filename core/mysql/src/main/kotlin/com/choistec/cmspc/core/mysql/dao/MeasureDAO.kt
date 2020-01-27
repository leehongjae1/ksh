package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface MeasureDAO {


//	@Select("""
//    SELECT
//    if(timestampdiff(second , dust_mod_date, utc_timestamp()) > 120, null, dust_data) as dust,
//    if(timestampdiff(second , co2_mod_date, utc_timestamp()) > 120, null, co2_data) as co2,
//    if(timestampdiff(second , temp_mod_date, utc_timestamp()) > 120, null, temp_data) as temp,
//    if(timestampdiff(second , humi_mod_date, utc_timestamp()) > 120, null, humi_data) as humi
//    FROM environment_last
//	WHERE
//    admin_id = #{$ADMIN_ID}
//	""")
//	fun seleteEnvironmentLast(
//			@Param(ADMIN_ID) admin_id: String): EnvironmentLast

	@Select("""
 	SELECT
	if(timestampdiff(second , last.dust_mod_date, utc_timestamp()) > 120, null, last.dust_data) as dust,
	if(timestampdiff(second , last.co2_mod_date, utc_timestamp()) > 120, null, last.co2_data) as co2,
	if(timestampdiff(second , last.temp_mod_date, utc_timestamp()) > 120, null, last.temp_data) as temp,
	if(timestampdiff(second , last.humi_mod_date, utc_timestamp()) > 120, null, last.humi_data) as humi
	FROM environment_last as last
	LEFT JOIN ward_info AS ward
	ON ward.device_id = last.device_id
	where ward.hostpital_id = #{$HOSTPITAL_ID}
	""")
	fun seleteEnvironmentLast(
			@Param(HOSTPITAL_ID) hostpital_id: String): EnvironmentLast


	@Select("""
    SELECT
    info.baby_id,
    info.baby_position_number,
    info.baby_name,
    info.rearer_name,
    info.device_id as did,
    measure.temp_battery, 
    DATE_ADD(measure.temp_mod_date, INTERVAL (-9)*(-1) hour) as temp_mod_date,
    DATE_ADD(measure.spo2_mod_date, INTERVAL (-9)*(-1) hour) as spo2_mod_date,
    DATE_ADD(measure.weight_mod_date, INTERVAL (-9)*(-1) hour) as weight_mod_date
    FROM baby_info AS info
    LEFT JOIN measure_last AS measure
    ON measure.did = info.device_id
    WHERE
    info.del_YN LIKE '0' and
    info.admin_id = #{$ADMIN_ID}
	""")
	fun seleteDeviceInfo(
			@Param(ADMIN_ID) admin_id: String): List<DeviceInfo>


	/*
	* 주변 정보의 마지막 값을 데이터 베이스에 저장한다.
	*/
	@Insert("""
    INSERT INTO ENVIRONMENT_LAST SET
    $ADMIN_ID=#{$ADMIN_ID},
	$DEVICE_ID=#{$DEVICE_ID},
    ${'$'}{bytes}
    ON DUPLICATE KEY UPDATE
    ${'$'}{bytes}
	""")
	fun insertEnviromentData(@Param(ADMIN_ID) admin_id: String?,
							 @Param(DEVICE_ID) device_id: Int?,
							 @Param("bytes") value: String)


	/*
* 주변 정보의 마지막 값을 데이터 베이스에 저장한다.
*/
	@Insert("""
    INSERT INTO ENVIRONMENT_MEASURE SET
    $ADMIN_ID=#{$ADMIN_ID},
	$DEVICE_ID=#{$DEVICE_ID},
    ${'$'}{bytes}
    ON DUPLICATE KEY UPDATE
    ${'$'}{bytes}
	""")
	fun insertEnviromentGraphData(@Param(ADMIN_ID) admin_id: String?,
							 @Param(DEVICE_ID) device_id: Int?,
							 @Param("bytes") value: String)

	/*
	* 측정된 마지막 데이터를 데이터 베이스에 저장한다.
	* 이 데이터는 실시간 데이터로 사용된다.
	*/
    @Insert("""
    INSERT INTO MEASURE_LAST SET
    $DID=#{$DID},
    ${'$'}{bytes}
    ON DUPLICATE KEY UPDATE
    ${'$'}{bytes}
	""")
	fun insertRealtimeData(@Param(DID) did: Int, @Param("bytes") value: String)

	/*
	* 아기 정보와 장비 정보를 데이터베이스에
	* 매칭 시킨다.
	*/
//	@SelectKey(statement=["""
//    SELECT
//    	$BABY_ID
//    FROM MAP_DEVICE
//    WHERE
//    	$DID=#{$DID}
//    """], keyProperty=BABY_ID, before=true, resultType=Int::class)


	@Insert("""
    INSERT INTO AUTO_${'$'}{$BABY_ID}_MEASURE SET
    ${'$'}{bytes}
	,$REG_DATE=$NOW
	""")
	fun insertData(@Param(BABY_ID) baby_id: String?, @Param("bytes") value: String)

	@Select("""
		select
         baby.admin_id,
		 baby.baby_id,
         baby.baby_name,
         device.device_id
         from baby_info as baby
         left join map_device as device on
         device.device_id = baby.device_id
         where
         device.device_id like #{$DID} and
         baby.del_yn like '0'
    """)
	fun deviceCheck(@Param(DID) device_id: String?): List<Map<String, Any>>

	@Select("""
		select 1
    """)
	fun select1(): List<Map<String, Any>>


	@Select("""
		select
         device.admin_id,
		 ward.ward_id,
         ward.ward_name,
         device.device_id
         from ward_info as ward
         left join map_device as device on
         device.device_id = ward.device_id
         where
         device.device_id like #{$DEVICE_ID}
    """)
	fun envDeviceCheck(@Param(DEVICE_ID) device_id: String?): List<Map<String, Any>>


	@Insert("""
    INSERT INTO baby_growth_last SET
    admin_id = #{admin_id},
    baby_id = #{baby_id},
    ${'$'}{bytes}
    ON DUPLICATE KEY UPDATE
    ${'$'}{bytes}
	""")
	fun insertGrowthInfoLast(@Param("admin_id") admin_id: String?,
							 @Param("baby_id") baby_id: String?,
							 @Param("bytes") value: String):Int


	@Insert("""
    INSERT INTO baby_growth SET
    admin_id = #{admin_id},
    baby_id = #{baby_id},
    ${'$'}{bytes}
    ON DUPLICATE KEY UPDATE
    ${'$'}{bytes}
	""")
	fun insertGrowthInfoGraph(@Param("admin_id") admin_id: String?,
							 @Param("baby_id") baby_id: String?,
							 @Param("bytes") value: String):Int

	/*
	*
	*/
	@Insert("""
    INSERT IGNORE INTO MAP_DEVICE SET
    $DID=#{$DID},
    $ADMIN_ID=#{$ADMIN_ID}
	""")
	fun map(@Param(DID) did: Int, @Param(ADMIN_ID) admin_id: String)

	/*
	* 관리자 아이디에 속해있는 장비 번호와
	* 아기 아이디를 불러온다.
	*/
	@Select("""
    SELECT
    	$DID,
    	$BABY_ID
    FROM MAP_DEVICE
    WHERE
    	$ADMIN_ID = #{$ADMIN_ID}
    """)
	fun selectDevicesWhereAdmin(@Param(ADMIN_ID) admin_id:String?):List<Map<String,Any>>

	/*
	* 아기 아이디에 속해 있는
	* 디바이스 아이디를 불러온다.
	*/
	@Select("""
    SELECT
    	$DID
    FROM MAP_DEVICE
    WHERE
    	$BABY_ID = #{$BABY_ID}
    """)
	fun selectDevices(@Param(BABY_ID) baby_id: Int?):List<Map<String,Any>>

	/*
	* 아기 아이디에 속해 있는
	* 카메라 아이디 정보를 불러온다.
	*/
	@Select("""
    SELECT
    	cam$_ID,
    	$ADMIN_ID
    FROM MAP_DEVICE
    WHERE
    	$BABY_ID = #{$BABY_ID}
    """)
	fun selectCamera(@Param(BABY_ID) baby_id: Int?):List<Map<String,Any>>

	/*
	* 데이터베이스의
	* 아기 아이디에 속해 있는
	* 디바이스 정보를
	* 다른 디바이스로 업데이트 시킨다.
    */
	@Update("""
    UPDATE MAP_DEVICE
    SET
    	$BABY_ID=#{$BABY_ID}
    WHERE
    	$DID=#{$DID} AND
    	$ADMIN_ID=#{$ADMIN_ID}
	""")
	fun register(@Param(BABY_ID) baby_id: Int, @Param(ADMIN_ID) admin_id: String, @Param(DID) did: Int)

	/*
	* 아기에 속해있는 디바이스 정보를
	* 아기 아이디를 널 값으로 만들어서
	* 무효화 시킨다.
	*/
	@Update("""
    UPDATE MAP_DEVICE
    SET
    	$BABY_ID=null
    WHERE
    	$BABY_ID=#{$BABY_ID} AND
    	$ADMIN_ID=#{$ADMIN_ID}
	""")
	fun unregister(@Param(BABY_ID) baby_id: Int, @Param(ADMIN_ID) admin_id: String)

}
