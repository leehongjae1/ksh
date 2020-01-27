package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface AdminDAO {
	/*관리자 아이디 등록*/
    @Insert("""
    INSERT IGNORE INTO $ADMIN$_USER
    ($ID,$PASSWORD,$NAME,$REG_DATE,$EMAIL)
    VALUES (#{$ADMIN.$ID},#{$ADMIN.$PASSWORD},#{$ADMIN.$NAME},$NOW,#{$ADMIN.$EMAIL})
	""")
	fun insert(@Param(ADMIN) admin: Admin)
   /*관리자 아이디 수정*/
	@Update("""
    UPDATE $ADMIN$_USER
    set $DEL = 1,
    $MOD_DATE = $NOW
    WHERE $ID = #{$ID}
    """)
	/*유저 아이디 삭제*/
	fun delete(@Param(ID) id:String)

	@Update("""
    UPDATE $ADMIN$_USER
    set $DEL = 0
    WHERE $ID = #{$ID}
    """)
	fun restore(@Param(ID) id:String)

	@Select("""
    SELECT
    	$ID,
    	$PASSWORD
    FROM $ADMIN$_USER
    WHERE
    	$ID = #{$ID} and
    	$DEL != 1
    """)
	fun selectPw(@Param(ID) id:String):List<Admin>

	@Select("""
    SELECT
    	$ID,
    	password,
    	$NAME,
    	email,
	    hospital_id
    FROM $ADMIN$_USER
    WHERE
    	$ID = #{$ID} and
    	$DEL != 1
    """)
	fun selectOne(@Param(ID) id:String):List<Admin>

	/*데이터 베이스에 저장된 환경정보 불러오기*/
	@Select("""
    SELECT
    	${DUST}_$DATA as $DUST,
    	${CO2}_$DATA as $CO2,
    	${TEMP}_$DATA as $TEMP,
    	${HUMI}_$DATA as $HUMI
	FROM environment_last
 	where
 		$ADMIN$_ID = #{$ADMIN$_ID}
	""")
	fun selectEnvironmentData(@Param(ADMIN_ID) admin_id:String):List<Admin.EnvironmentData>

}
