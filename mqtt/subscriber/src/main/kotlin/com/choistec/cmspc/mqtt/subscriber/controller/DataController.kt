package com.choistec.cmspc.mqtt.subscriber.controller


import com.choistec.cmspc.core.dto.ADMIN_ID
import com.choistec.cmspc.core.dto.MAC
import com.choistec.cmspc.core.mysql.dao.MeasureDAO
import com.choistec.cmspc.core.mysql.dao.ScannerDAO
import com.choistec.cmspc.core.mysql.dto.Environment
import com.choistec.cmspc.core.mysql.dto.Growth
import com.choistec.cmspc.core.mysql.dto.Measure
import com.choistec.cmspc.core.mysql.dto.Measure.Companion.now
import com.choistec.cmspc.mqtt.subscriber.config.DatabaseConfig
import com.choistec.cmspc.mqtt.subscriber.config.DatabaseConfig.session
import com.choistec.cmspc.mqtt.subscriber.controller.Beacon.Companion.parseRawData
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

const val DATA_VIEW_INTERVAL = 5
const val DATA_SAVE_INTERVAL = 30
const val ENVIROMENT_DATA_VIEW_INTERVAL = 5
const val ENVIROMENT_DATA_SAVE_INTERVAL = 30
const val GROWTH_DATA_SAVE_INTERVAL = 5
var viewTime = now()
var saveTime = now()
var enviromentViewTime = now()
var enviromentSaveTime = now()
var growthSaveTime = now()

object DataController {

    private val scannerDAO by lazy { DatabaseConfig.getDao<ScannerDAO>() }
    private val measureDAO by lazy { DatabaseConfig.getDao<MeasureDAO>() }

    private var scannerList: MutableList<Map<String, String>> = arrayListOf()
        get() = if (field.isEmpty()) {
            scannerDAO.select()
                    .groupBy {
                        it[ADMIN_ID] ?: ""
                    }
                    .map {
                        mapOf(
                                ADMIN_ID to it.key,
                                MAC to it.value.joinToString(",") { it[MAC] ?: "" }
                        )
                    }
                    .toMutableList()
                    .apply {
                        forEach(::println)
                        field = this
                    }
        } else {
            field
        }

    private val growthMap = HashMap<Int, Growth>()
    private val growthMapTemp = HashMap<Int, Growth>()
    private val growthMapSave = HashMap<Int, Growth>()
    private val envMap = HashMap<Int, Environment>()
    private val envMapView = HashMap<Int, Environment>()
    private val envMapGraph = HashMap<Int, Environment>()
    private val dataMap = HashMap<Int, Measure>()
    private val dataMapView = HashMap<Int, Measure>()
    private val dataMapGraph = HashMap<Int, Measure>()

    var checkDataReceive:Boolean = false
    var mScanTimer = Timer()

    enum class Topics(
            val url: String,
            val process: (ByteArray) -> Unit
    ) {
        BaseTopic("/CMS_HUB/BIO_SIGNAL", {
            //println("${Date()}${BaseTopic.url}   ${it.toHex(",")}")
            //들어옴
            //println("데이터 들어옴")

            val (mac, list) = parseRawData(it)

            if(!checkDataReceive) {
                checkDataReceive = true
                mScanTimer = Timer()
                var mTask = object : TimerTask() {
                    override fun run() {
                        println("1시간 마다 실행중")
                        measureDAO.select1()
                        session?.commit()
                    }
                }
                mScanTimer.schedule(mTask, 5000, 3600000)
            }

            dataProcess("testId", list)

        });

        companion object {
            operator fun get(value: String) = values().firstOrNull { it.url == value }
        }
    }



    private fun dataProcess(id: String, list: List<Beacon.Data?>) {
        list.groupBy {

            when (it?.beacon) {
                Beacon.Phone -> {
                    Process.MobileProcess
                }

                Beacon.Co2TempHumi,
                Beacon.Dust -> {
                    Process.EnvironmentProcess
                }
                Beacon.NurseUrineFecesVomit ->{
                    Process.GrowthProcess
                }
                Beacon.TempBreath,
                Beacon.Spo2Heart,
                Beacon.Weight -> {
                    Process.MeasureProcess
                }
                else -> Process.Null
            }
        }.forEach {
            it.key(id, it.value)
        }
    }

    @Throws(MySQLSyntaxErrorException::class)//no user so no auto generated table
    fun dataSave() = dataMap
            .takeIf { it.isNotEmpty() }
            ?.iterator()
            ?.run {
//                while (hasNext()) {
//                    val (key, value) = next()
//                    /***/
//                    if (now() - value.time < DATA_SAVE_INTERVAL) {
//                        try {
//                            measureDAO.insertData(key, value.toQuery())
//                        } catch (e: MySQLSyntaxErrorException) {
//                            println("Syntax:No User - did = $key")
//                        } catch (e: org.apache.ibatis.exceptions.PerysistenceException) {
//                            println("Persist:No User - did = $key")
//                        }
//                    } else {
//                        measureDAO.insertRealtimeData(key, Measure.nullQuery())
//                        remove()
//                    }
//                }
//                session?.commit()
            }

    fun envClean() = envMap
            .takeIf { it.isNotEmpty() }
            ?.iterator()
            ?.run {
                while (hasNext()) {
                    val (key, value) = next()
                    if (now() - value.time < DATA_SAVE_INTERVAL) {
                        //still running
                    } else {
                        //measureDAO.insertEnviromentData("testId", Environment.nullQuery())
                        envMap.remove(key)
                    }
                }
                session?.commit()
            }

    enum class Process {
        MobileProcess {
            override fun invoke(adminId: String, data: List<Beacon.Data?>) {
            }
        },
        EnvironmentProcess {
            /*주변 환경 정보를 바이트로 받아 가공하는 구간*/
            override fun invoke(adminId: String, data: List<Beacon.Data?>) {
                data.filter {
                    it?.id != null
                }
                        .groupBy { it?.id ?: -1 }
                        .forEach { did, beaconData ->
                            (envMap[did] ?: Environment().apply {
                                envMap[did] = this
                                //measureDAO.map(did,adminId)
                                session?.commit()
                            })
                                    .apply {
                                        beaconData.forEach {
                                            val device = measureDAO.envDeviceCheck(it?.id.toString())

                                            admin_id = device.firstOrNull()?.get("admin_id")?.toString()
                                            device_id = did

                                            if (device.firstOrNull()?.get("admin_id")?.toString() == null) {

                                            } else {

                                                when (it?.beacon) {
                                                    Beacon.Co2TempHumi -> {
                                                        co2_data = it.values[0].toFloat().takeIf { it in -60.0..3000.0 }
                                                        temp_data = it.values[1].toFloat()
                                                        humi_data = it.values[2].toFloat()
                                                    }
                                                    Beacon.Dust -> {
                                                        dust_data = it.values[0].toFloat().takeIf { it in -60.0..1000.0 }
                                                    }
                                                    else -> {
                                                    }
                                                }

                                                envMapView[did] = this
                                                envMapGraph[did] = this
                                            }
                                        }
                                        if (now() - enviromentViewTime > ENVIROMENT_DATA_VIEW_INTERVAL) {
                                            enviromentViewTime = now()
                                            envMapView.takeIf { it.isNotEmpty() }
                                                    ?.iterator()
                                                    ?.run {
                                                        //테스트 - 시간 기록 - 환경 정보 5초
                                                        //println("환경 정보 5초데이터 저장")
                                                        while (hasNext()) {
                                                            val (key, value) = next()
                                                            try {
                                                                //테스트 - 환경정보 디바이스 아이디 키값
                                                                //println("환경정보 key 값 $key")

                                                                //테스트 - 특정 아이디 - 환경정보 5초 저장 로그 검사
                                                                //if(key == 90003) {
//                                                                    println("환경정보 key 값 $key")
//                                                                    println("value.toRealTimeQuery() ${value.toRealTimeQuery()}")
//                                                                    println("adminId ${value.admin_id}")
                                                                //}
                                                                measureDAO.insertEnviromentData(value.admin_id, key, value.toRealTimeQuery())
                                                            } catch (e: MySQLSyntaxErrorException) {
                                                                //println("Syntax:No User - did = ${e.message}")
                                                                //println("Syntax:No User - did = $key")
                                                            } catch (e: org.apache.ibatis.exceptions.PersistenceException) {
                                                                //println("Syntax:No User - did = ${e.message}")
                                                                //println("Persist:No User - did = $key")
                                                            }
                                                        }
                                                        session?.commit()
                                                        envMapView.clear()
                                                    }
                                        }

                                        if (now() - enviromentSaveTime > ENVIROMENT_DATA_SAVE_INTERVAL) {
                                            enviromentSaveTime = now()
                                            envMapGraph.takeIf { it.isNotEmpty() }
                                                    ?.iterator()
                                                    ?.run {
                                                        //테스트 - 시간 기록 - 환경정보 그래프 데이터 저장
                                                        //밑의 주석을 제거한다.
                                                        //println("30초데이터 저장")
                                                        while (hasNext()) {
                                                            val (key, value) = next()
                                                            /***/
                                                            try {
                                                                measureDAO.insertEnviromentGraphData(value.admin_id, key, value.toGraphQuery())
                                                            } catch (e: MySQLSyntaxErrorException) {
                                                                println("Syntax:No User - did = ${e.message}")
                                                                //println("Syntax:No User - did = $key")
                                                            } catch (e: org.apache.ibatis.exceptions.PersistenceException) {
                                                                //println("Persist:No User - did = $key")
                                                                println("Persist:No User - did = ${e.message}")
                                                            }
                                                        }
                                                        session?.commit()
                                                        envMapGraph.clear()
                                                    }


                                        }


                                    }
                        }
            }
        },
        GrowthProcess {
            /*주변 환경 정보를 바이트로 받아 가공하는 구간*/
            override fun invoke(adminId: String, data: List<Beacon.Data?>) {
                data.filter {
                    it?.id != null
                }
                        .groupBy { it?.id ?: -1 }
                        .forEach { did, beaconData ->
                            (growthMap[did] ?: Growth().apply {
                                growthMap[did] = this
                                //measureDAO.map(did,adminId)
                                session?.commit()
                            })
                                    .apply {
                                        beaconData.forEach {
                                            val device = measureDAO.deviceCheck(it?.id.toString())

                                            admin_id = device.firstOrNull()?.get("admin_id")?.toString()
                                            baby_id = device.firstOrNull()?.get("baby_id")?.toString()
                                            device_id = did

                                            if (device.firstOrNull()?.get("admin_id")?.toString() == null) {

                                            } else {

                                                when (it?.beacon) {
                                                    Beacon.NurseUrineFecesVomit -> {
                                                        nurse_index_data = it.values[0].toFloat()
                                                        nurse_data = it.values[1].toFloat()
                                                        urine_index_data = it.values[2].toFloat()
                                                        urine_data = it.values[3].toFloat()
                                                        feces_index_data = it.values[4].toFloat()
                                                        feces_data = it.values[5].toFloat()
                                                        vomit_index_data = it.values[6].toFloat()
                                                        vomit_data = it.values[7].toFloat()
                                                    }
                                                    else -> {
                                                    }
                                                }

                                                //테스트 - 특정 아이디 - 발육정보 데이터 들어오는지 체크
//                                                if(did==61011) {
//                                                    println("-------------------------------")
//                                                    println("nurse_index_data : ${nurse_index_data}")
//                                                    println("nurse_data : ${nurse_data}")
//                                                    println("urine_index_data : ${urine_index_data}")
//                                                    println("urine_data : ${urine_data}")
//                                                    println("feces_index_data : ${feces_index_data}")
//                                                    println("feces_data : ${feces_data}")
//                                                    println("vomit_index_data : ${vomit_index_data}")
//                                                    println("vomit_data : ${vomit_data}")
//                                                }

                                                if(growthMapTemp[did] == null){
                                                    println("널 값 아님")
                                                    growthMapTemp[did] = Growth()

                                                    growthMapTemp[did]?.admin_id = admin_id
                                                    growthMapTemp[did]?.baby_id = baby_id
                                                    growthMapTemp[did]?.device_id = device_id
                                                    growthMapTemp[did]?.nurse_index_data = 9999.toFloat()
                                                    growthMapTemp[did]?.nurse_data = 0.toFloat()

                                                    growthMapTemp[did]?.feces_index_data = 9999.toFloat()
                                                    growthMapTemp[did]?.feces_data = 0.toFloat()

                                                    growthMapTemp[did]?.urine_index_data = 9999.toFloat()
                                                    growthMapTemp[did]?.urine_data = 0.toFloat()

                                                    growthMapTemp[did]?.vomit_index_data = 9999.toFloat()
                                                    growthMapTemp[did]?.vomit_data = 0.toFloat()

                                                }else{
                                                    growthMapTemp[did]?.nurse_data = 0.toFloat()
                                                    growthMapTemp[did]?.feces_data = 0.toFloat()
                                                    growthMapTemp[did]?.urine_data = 0.toFloat()
                                                    growthMapTemp[did]?.vomit_data = 0.toFloat()

                                                    if(growthMapTemp[did]?.nurse_index_data != nurse_index_data){
                                                        var dataInt: Int = nurse_data?.toInt()!!
                                                        growthMapTemp[did]?.nurse_data = growthMapTemp[did]?.nurse_data?.plus(dataInt)
                                                        growthMapTemp[did]?.nurse_index_data = nurse_index_data
                                                        measureDAO.insertGrowthInfoGraph(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())
                                                        measureDAO.insertGrowthInfoLast(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())

                                                        println("들어온 nurse 데이터 ${nurse_data}")
                                                        println("nurse 데이터 삽입 결과 ${growthMapTemp[did]?.nurse_data}")
                                                        session?.commit()
                                                    }else{
                                                        //println("nurse 동일한 인덱스")



                                                    }

                                                    if(growthMapTemp[did]?.feces_index_data != feces_index_data){
                                                        var dataInt: Int = feces_data?.toInt()!!
                                                        growthMapTemp[did]?.feces_data = growthMapTemp[did]?.feces_data?.plus(dataInt)
                                                        growthMapTemp[did]?.feces_index_data = feces_index_data

                                                        measureDAO.insertGrowthInfoGraph(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())
                                                        measureDAO.insertGrowthInfoLast(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())

                                                        println("들어온 feces 데이터 ${feces_data}")
                                                        println("feces 데이터 삽입 결과 ${growthMapTemp[did]?.feces_data}")
                                                        session?.commit()
                                                    }else{
                                                        //println("feces 동일한 인덱스")
                                                    }

                                                    if(growthMapTemp[did]?.urine_index_data != urine_index_data){
                                                        var dataInt: Int = urine_data?.toInt()!!
                                                        growthMapTemp[did]?.urine_data = growthMapTemp[did]?.urine_data?.plus(dataInt)
                                                        growthMapTemp[did]?.urine_index_data = urine_index_data

                                                        measureDAO.insertGrowthInfoGraph(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())
                                                        measureDAO.insertGrowthInfoLast(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())

                                                        println("들어온 urine 데이터 ${urine_data}")
                                                        println("urine 데이터 삽입 결과 ${growthMapTemp[did]?.urine_data}")
                                                        session?.commit()
                                                    }else{
                                                        //println("urine 동일한 인덱스")
                                                    }

                                                    if(growthMapTemp[did]?.vomit_index_data != vomit_index_data){
                                                        var dataInt: Int = vomit_data?.toInt()!!
                                                        growthMapTemp[did]?.vomit_data = growthMapTemp[did]?.vomit_data?.plus(dataInt)
                                                        growthMapTemp[did]?.vomit_index_data = vomit_index_data
                                                        measureDAO.insertGrowthInfoGraph(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())
                                                        measureDAO.insertGrowthInfoLast(growthMapTemp[did]?.admin_id, growthMapTemp[did]?.baby_id, growthMapTemp[did]!!.toRealTimeQuery())

                                                        println("들어온 vomit 데이터 ${vomit_data}")
                                                        println("vomit 데이터 삽입 결과 ${growthMapTemp[did]?.vomit_data}")
                                                        session?.commit()
                                                    }else{
                                                        //println("vomit 동일한 인덱스")
                                                    }
                                                }

                                                growthMapSave[did] = this
                                            }
                                        }


//                                        if (now() - growthSaveTime > GROWTH_DATA_SAVE_INTERVAL) {
//                                            growthSaveTime = now()
//                                            growthMapTemp.takeIf { it.isNotEmpty() }
//                                                    ?.iterator()
//                                                    ?.run {
//                                                        //테스트 - 시간 기록 - 발육정보 그래프 데이터 저장
//                                                        //밑의 주석을 제거한다.
//                                                        //println("30초데이터 저장")
//                                                        while (hasNext()) {
//                                                            val (key, value) = next()
//                                                            /***/
//                                                            try {
//                                                                measureDAO.insertGrowthInfoGraph(value.admin_id, value.baby_id, value.toRealTimeQuery())
//                                                                measureDAO.insertGrowthInfoLast(value.admin_id, value.baby_id, value.toRealTimeQuery())
//                                                            } catch (e: MySQLSyntaxErrorException) {
//                                                                println("Syntax:No User - did = ${e.message}")
//                                                                //println("Syntax:No User - did = $key")
//                                                            } catch (e: org.apache.ibatis.exceptions.PersistenceException) {
//                                                                //println("Persist:No User - did = $key")
//                                                                println("Persist:No User - did = ${e.message}")
//                                                            }
//                                                        }
//                                                        session?.commit()
//                                                        growthMapTemp.clear()
//                                                    }
//
//
//                                        }


                                    }
                        }
            }
        },
        MeasureProcess {
            override fun invoke(adminId: String, data: List<Beacon.Data?>) {
                data.filter {
                    it?.id != null
                }
                        .groupBy { it?.id ?: -1 }
                        .forEach { did, beaconData ->
                            (dataMap[did] ?: Measure().apply {
                                dataMap[did] = this
                                //measureDAO.map(did,adminId)
                                session?.commit()
                            })
                                    .apply {
                                        beaconData.forEach {


                                            val device = measureDAO.deviceCheck(it?.id.toString())
                                            //println("ID ${it?.id.toString()}")
                                            //println("baby_name ${device.firstOrNull()?.get("baby_name")?.toString()}")

                                            if (device.firstOrNull()?.get("baby_name")?.toString() == null) {

                                            } else {
                                                //println("${device.firstOrNull()?.get("baby_name")?.toString()}")

                                                when (it?.beacon) {
                                                    //온도, 호흡수
                                                    Beacon.TempBreath -> {
                                                        temp_data = it.values[0].toFloat().takeIf { it in -60.0..-0.1 || it in 0.1..50.0}
                                                        temp_battery = it.battery
                                                        breath_data = it.values[1].toFloat().takeIf { it in 0.0..120.0 }
                                                        breath_battery = it.battery
                                                    }
                                                    Beacon.Spo2Heart -> {
                                                        spo2_data = it.values[0].toFloat().takeIf { it in 0.1..100.0 }
                                                        spo2_battery = it.battery
                                                        heart_data = it.values[1].toFloat().takeIf { it in 0.1..200.0 }
                                                        heart_battery = it.battery
                                                    }
                                                    Beacon.Weight -> {
                                                        weight_data = it.values[0].toFloat().takeIf { it in 0.0..30.0 }
                                                        weight_battery = it.battery
                                                    }
                                                    else -> {
                                                    }
                                                }
                                                baby_id = device.firstOrNull()?.get("baby_id")?.toString()
                                                dataMapView[did] = this
                                                dataMapGraph[did] = this

                                            }
                                        }

                                        if (now() - viewTime > DATA_VIEW_INTERVAL) {
                                            viewTime = now()
                                            dataMapView.takeIf { it.isNotEmpty() }
                                                    ?.iterator()
                                                    ?.run {
                                                        //테스트 - 시간 기록 - 생체 정보 5초
                                                        //println("생체 정보 5초데이터 저장")
                                                        while (hasNext()) {
                                                            val (key, value) = next()
                                                            try {
                                                                //테스트 - 특정 아이디 - 최종 저장 쿼리문 로그
//                                                                if(key==61011) {
//                                                                    println("weight_data : ${value.weight_data}")
//                                                                    println("value toRealTimeQuery ${value.toRealTimeQuery()}")
//                                                                }

                                                                //println("$did ${key}")
                                                                measureDAO.insertRealtimeData(key, value.toRealTimeQuery())
                                                            } catch (e: MySQLSyntaxErrorException) {
                                                                println("Syntax:No User - did = $key")
                                                            } catch (e: org.apache.ibatis.exceptions.PersistenceException) {
                                                                println("Persist:No User - did = $key")
                                                            }
                                                        }
                                                        session?.commit()
                                                        dataMapView.clear()
                                                    }


                                        }

                                        if (now() - saveTime > DATA_SAVE_INTERVAL) {
                                            saveTime = now()
                                            dataMapGraph.takeIf { it.isNotEmpty() }
                                                    ?.iterator()
                                                    ?.run {
                                                        //테스트 - 시간 기록 - 생체 정보 30초
                                                        //println("생체 정보 30초데이터 저장")
                                                        while (hasNext()) {
                                                            val (key, value) = next()
                                                            /***/
                                                                try {
                                                                    //테스트 - 특정 아이디 - 생체 정보 저장 쿼리문
//                                                                    if(key == 61011){
//                                                                        println("weight_data : ${value.weight_data}")
//                                                                        println("value toRealTimeQuery ${value.toRealTimeQuery()}")
//                                                                        //println("baby id ${value.baby_id} valueQuery ${value.toQuery()}")
//                                                                    }
                                                                    measureDAO.insertData(value.baby_id, value.toQuery())

                                                                } catch (e: MySQLSyntaxErrorException) {
                                                                    println("Syntax:No User - did = ${e.message}")
                                                                    //println("Syntax:No User - did = $key")
                                                                } catch (e: org.apache.ibatis.exceptions.PersistenceException) {
                                                                    //println("Persist:No User - did = $key")
                                                                    println("Persist:No User - did = ${e.message}")
                                                                }
                                                        }
                                                        session?.commit()
                                                        dataMapGraph.clear()
                                                    }


                                        }


                                    }
                        }
            }
        },
        Null {
            override fun invoke(adminId: String, data: List<Beacon.Data?>) {}
        };

        abstract operator fun invoke(adminId: String, data: List<Beacon.Data?>)
    }
}
