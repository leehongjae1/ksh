package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.Staff
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param

@Mapper
interface StaffDAO {

    @Insert("""
    INSERT INTO medical_staff
    (
    medical_staff_id,
    password,
    medical_staff_name,
    medical_staff_contact,
    medical_staff_email,
    group_priority,
    position_info_id,
    medical_department_id,
    ward_id,
    reg_date, 
    mod_date,
    del_yn
    )
    VALUES
    (
    #{staff.medical_staff_id},
    #{staff.password},
    #{staff.medical_staff_name},
    #{staff.medical_staff_contact},
    #{staff.medical_staff_email},
    #{staff.group_priority},
    #{staff.position_info_id},
    #{staff.medical_department_id},
    #{staff.ward_id},
    #{staff.reg_date},
    #{staff.mod_date},
    #{staff.del_yn}
    )
     """)
    @Options(useGeneratedKeys = true, keyProperty = "staff.medical_staff_id")
    fun insertStaffInfo(@Param("staff") staff: Staff): Int


}
