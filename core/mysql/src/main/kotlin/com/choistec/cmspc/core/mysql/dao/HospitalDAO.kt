package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.ID
import com.choistec.cmspc.core.dto.MEDICAL_DEPARTMENT_ID
import com.choistec.cmspc.core.dto.REARER
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

@Mapper
interface HospitalDAO {
	/**
	 * 진료과 정보 모두 호출
	 */
	@Select("""
    SELECT
		medical_department_id,
        medical_department_name,
		del_yn
	FROM medical_department
	""")
	fun selectMedicalDepartment():List<Map<String,Any?>>

	/**
	 * 진료과에 소속된 병동 정보 호출
	 */
	@Select("""
    SELECT
    	ward.ward_id,
		ward.ward_name,
		ward.medical_department_id,
		ward.del_yn
    FROM ward as ward
	left join medical_department as department
    on ward.medical_department_id = department.medical_department_id
    WHERE
    	ward.medical_department_id = #{$MEDICAL_DEPARTMENT_ID} AND
	    ward.del_yn = 0 AND
		department.del_yn =0
    """)
	fun selectWard(
			@Param(MEDICAL_DEPARTMENT_ID) id:Int
			):List<Map<String,Any?>>

	@Select("""
    SELECT
    	room.room_id,
		room.room_name,
		room.del_yn
    FROM room as room
	left join medical_department as department
    on room.medical_department_id = department.medical_department_id
	left join ward as ward
    on room.ward_id = ward.ward_id		
    WHERE
    	room.medical_department_id = #{medical_department_id} AND
	    room.ward_id = #{ward_id} AND
	    room.del_yn = 0 AND
		department.del_yn = 0 AND
		ward.del_yn = 0
    """)
	fun selectRoom(
			@Param("medical_department_id") medical_department_id:Int,
			@Param("ward_id") ward_id:Int
	):List<Map<String,Any?>>

	@Select("""
    SELECT
		position_info_id,
        position_info_name
	FROM position_info
	""")
	fun selectMedicalPosition():List<Map<String,Any?>>

	@Select("""
    SELECT
		blood_type_id,
        blood_type_name
	FROM blood_type
	""")
	fun selectBloodType():List<Map<String,Any?>>



}
