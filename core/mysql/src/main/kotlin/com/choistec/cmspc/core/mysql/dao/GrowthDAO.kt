package com.choistec.cmspc.core.mysql.dao

import com.choistec.cmspc.core.dto.*
import org.apache.ibatis.annotations.*

@Mapper
interface GrowthDAO {
//    @Insert("""
//    INSERT IGNORE INTO ${BABY}_growth
//    ($BABY$_ID,nurse,feces,urine,vomit,$REG_DATE)
//    VALUES (#{growth.$BABY_ID},#{growth.nurse},#{growth.feces},#{growth.urine},#{growth.vomit},$NOW)
//	""")
//	@Options(useGeneratedKeys = true, keyProperty = "growth.$ID")
//	fun insert(@Param("growth") growth: Baby.Growth):Int

	@Insert("""
        INSERT INTO baby_growth
        (
        admin_id, 
        baby_id, 
        nurse, 
        nurse_mod_date, 
        feces, 
        feces_mod_date, 
        urine, 
        urine_mod_date, 
        vomit, 
        vomit_mod_date, 
        reg_date
        ) 
        VALUES (
        #{growth.admin_id}, 
        #{growth.baby_id}, 
        #{growth.nurse}, 
        #{growth.nurse_mod_date}, 
        #{growth.feces}, 
        #{growth.feces_mod_date}, 
        #{growth.urine}, 
        #{growth.urine_mod_date}, 
        #{growth.vomit}, 
        #{growth.vomit_mod_date}, 
        $NOW)
	""")
	@Options(useGeneratedKeys = true, keyProperty = "growth.baby_id")
	fun insert(@Param("growth") growth: Baby.Growth):Int

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

	@Insert("""
        INSERT INTO baby_growth
        (
        admin_id, 
        baby_id, 
        nurse, 
        nurse_mod_date, 
        feces, 
        feces_mod_date, 
        urine, 
        urine_mod_date, 
        vomit, 
        vomit_mod_date, 
        reg_date
        ) 
        VALUES (
        #{growth.admin_id}, `
        #{growth.baby_id}, 
        #{growth.nurse}, 
        ifnull(#{growth.nurse_mod_date},$NOW),
        #{growth.feces}, 
        ifnull(#{growth.feces_mod_date},$NOW),
        #{growth.urine}, 
        ifnull(#{growth.urine_mod_date},$NOW),
        #{growth.vomit}, 
        ifnull(#{growth.vomit_mod_date},$NOW),
        $NOW
		)
	""")
	@Options(useGeneratedKeys = true, keyProperty = "growth.baby_id")
	fun insertGrowthInfo(@Param("growth") growth: GrowthInfo):Int



//	@Select("""
//    SELECT
//    	growth.$BABY$_ID,
//		growth.nurse,
//		growth.feces,
//		growth.urine,
//		growth.vomit
//    FROM $REARER$_USER as $REARER
//    LEFT JOIN $BABY$_USER as $BABY
//    	ON $REARER.$ID = $REARER_ID
//    LEFT JOIN ${BABY}_growth as growth
//    	ON $BABY.$ID = $BABY_ID
//    WHERE
//    	$ADMIN_ID = #{$ADMIN_ID} and
//    	$BABY.$DEL != 1 and
//    	growth.$REG_DATE >= DATE_ADD(timestamp(current_date), INTERVAL (#{offset})*(-1) hour)
//    """)
//	fun selectWhereAdmin(
//            @Param(ADMIN_ID) admin_id:String,
//            @Param("offset")offset:Float
//	 ):List<Baby.Growth>

	@Select("""
    SELECT
    	growth.baby_id,
		growth.nurse,
		growth.feces,
		growth.urine,
		growth.vomit,
        MAX(DATE_ADD(growth.reg_date, INTERVAL (#{offset})*(-1) hour)) as reg_date
    FROM baby_info as baby
    LEFT JOIN baby_growth as growth
    	ON growth.baby_id = baby.baby_id
    WHERE
    	baby.admin_id = #{admin_id} and
    	growth.reg_date = (select max(growth.reg_date))	
    GROUP BY growth.baby_id
    ORDER BY growth.reg_date DESC
    """)
	fun selectWhereAdmin(
			@Param(ADMIN_ID) admin_id:String,
			@Param("offset")offset:Float
	):List<Baby.Growth>

	@Select(""" 
    SELECT
    info.baby_position_number,
	info.admin_id,
    info.baby_id,
    info.baby_sex,
    info.rearer_name,
    ifnull(SUM(growth.nurse),0) as nurse,
    growth.nurse_mod_date,
    ifnull(SUM(growth.feces),0) as feces,
    growth.feces_mod_date,
    ifnull(SUM(growth.urine),0) as urine,
    growth.urine_mod_date,
    ifnull(SUM(growth.vomit),0) as vomit,
    growth.vomit_mod_date,
    measure.weight_data as weight,
    measure.weight_mod_date
    FROM baby_info as info
    LEFT JOIN baby_growth AS growth
    ON growth.baby_id = info.baby_id
    AND DATE_FORMAT(date_add(growth.reg_date, INTERVAL (-9)*(-1) hour), '%Y-%m-%d') LIKE DATE_FORMAT(date_add(utc_timestamp(), INTERVAL (-9)*(-1) hour), '%Y-%m-%d')
    LEFT JOIN measure_last AS measure
    ON measure.did = info.device_id
    WHERE info.del_yn LIKE '0' AND
    info.admin_id = #{$ADMIN_ID}
    group by info.baby_id
    """)
	fun seleteGrowthLastData(
			@Param(ADMIN_ID) admin_id:String
	):List<GrowthLastData>


	@Select("""
    use smart_cradle;
    SELECT
    admin_id,
    baby_id,
    round(sum(`nurse`),0) AS `nurse`,
    round(sum(`feces`),0) AS `feces`,
    round(sum(`urine`),0) AS `urine`,
    round(sum(`vomit`),0) AS `vomit`,
    DATE_FORMAT(DATE(DATE_ADD(`reg_date`, INTERVAL (-9)*(-1) hour)), '%Y-%m-%d %T') AS `date`
    FROM baby_growth
    WHERE
    baby_id LIKE #{$BABY_ID}
    GROUP BY `date`;
    """)
	fun seleteGraphDay(
			@Param(BABY_ID) baby_id:String
	):List<GrowthlGraph>


	@Select("""
    SELECT
    admin_id,
    baby_id,
    SUM(nurse) as nurse,
    nurse_mod_date,
    SUM(feces) as feces,
    feces_mod_date,
    SUM(urine) as urine,
    urine_mod_date,
    SUM(vomit) as vomit,
    vomit_mod_date,
    DATE_FORMAT(date_add(`reg_date`, INTERVAL (-9)*(-1) hour), '%Y-%m-%d %H:00:00') as date
    FROM baby_growth
    WHERE
    baby_id LIKE #{$BABY_ID} and
    DATE_FORMAT(DATE_ADD(`reg_date`, INTERVAL (-9)*(-1) hour), '%Y-%m-%d') LIKE #{$SEARCH_DATE}
    group by DATE_FORMAT(date_add(reg_date, INTERVAL (-9)*(-1) hour), '%Y-%m-%d %h')
    """)
	fun seleteGraphTime(
			@Param(BABY_ID) baby_id:String,
			@Param(SEARCH_DATE) search_date:String
			):List<GrowthlGraph>


	@Select("""
    SELECT
    	$BABY$_ID,
		nurse,
		feces,
		urine,
		vomit
    FROM ${BABY}_growth
    WHERE
    	$BABY_ID = #{$BABY_ID} and
    	$REG_DATE >= DATE_ADD(timestamp(current_date), INTERVAL (#{offset})*(-1) hour)
    """)
	fun selectOne(
            @Param(BABY_ID) id:Int,
            @Param("offset")offset:Float
	):List<Baby.Growth>

	@Select("""
    SELECT
    	DATE_ADD(timestamp($REG_DATE), INTERVAL #{offset} hour) as $REG_DATE,
		nurse,
		feces,
		urine,
		vomit
    FROM ${BABY}_growth
    WHERE
    	$BABY_ID = #{$BABY_ID}
    """)
	fun selectAllData(
            @Param(BABY_ID) id:Int,
            @Param("offset")offset:Float
	):List<Map<String,Any?>>
}
