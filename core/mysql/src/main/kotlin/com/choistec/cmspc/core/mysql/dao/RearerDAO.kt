package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface RearerDAO {
    @Insert("""
    INSERT IGNORE INTO $REARER$_USER
    ($ADMIN_ID,$NAME,$CONTACT,$BIRTHDAY,$REG_DATE)
    VALUES (#{$ADMIN_ID},#{$REARER.$NAME},#{$REARER.$CONTACT},#{$REARER.$BIRTHDAY},$NOW)
	""")
	@Options(useGeneratedKeys = true, keyProperty = "$REARER.$ID")
	fun insert(@Param(ADMIN_ID) admin_id: String, @Param(REARER) rearer: Rearer)

	@Update("""
    UPDATE $REARER$_USER
    set $DEL = 1,
    $MOD_DATE = $NOW
    WHERE $ID = #{$ID}
    """)
	fun delete(@Param(ID) id:Int)

	@Update("""
    UPDATE $REARER$_USER
    set $DEL = 0
    WHERE $ID = #{$ID}
    """)
	fun restore(@Param(ID) id:Int)

	@Select("""
    SELECT
    	$ID,
    	$NAME,
    	$CONTACT,
    	$BIRTHDAY
    FROM $REARER$_USER
    WHERE
    	$ADMIN_ID = #{$ADMIN_ID} and
    	$DEL != 1
    """)
	fun select(@Param(ADMIN_ID) admin_id:String):List<Rearer>

	@Select("""
    SELECT
    	$ID,
    	$NAME,
    	$CONTACT,
    	$BIRTHDAY
    FROM $REARER$_USER
    WHERE
    	$ADMIN_ID = #{$ADMIN_ID} and
    	$ID = #{$ID} and
    	$DEL != 1
    """)
	fun selectOne(@Param(ADMIN_ID) admin_id:String, @Param(ID) id:Int):List<Rearer>

	@Select("""
    SELECT
    	$ID,
    	$NAME,
    	$CONTACT,
    	$BIRTHDAY
    FROM $REARER$_USER
    WHERE
    	$CONTACT = #{$CONTACT} and
    	$BIRTHDAY = #{$BIRTHDAY} and
    	$DEL != 1
    """)
	fun selectOneWithValidate(@Param(CONTACT) contact:String, @Param(BIRTHDAY) birthday:String):List<Rearer>
}
