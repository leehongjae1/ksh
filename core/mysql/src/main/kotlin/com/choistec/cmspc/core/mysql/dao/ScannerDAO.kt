package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface ScannerDAO {
    @Insert("""
    INSERT INTO MAP_${ADMIN}_SCANNER
    ($MAC,$ADMIN_ID)
    VALUES (#{$MAC},#{$ADMIN_ID})
	""")
	fun insert(@Param(ADMIN_ID) admin_id: String, @Param(MAC) mac: String)

	@Delete("""
    DELETE FROM MAP_${ADMIN}_SCANNER
    WHERE $ID = #{$ID}
    """)
	fun delete(@Param(ID) id:String)

	@Select("""
    SELECT
    	$ID,
    	$MAC,
    	$ADMIN$_ID
    FROM MAP_${ADMIN}_SCANNER
    """)
	fun select():MutableList<Map<String,String>>
}
