package com.choistec.cmspc.core.option

import com.choistec.cmspc.core.define.BaseDefine
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileNotFoundException
import javax.naming.NoInitialContextException

class BaseOption{
    enum class MQTT(
            val value:String,
            val explain:String
    ){
        LogLevel(BaseDefine.LogMode.Debug.ordinal.toString(),"Debug=0 or Default=1 or Mute=2"),
        Domain(BaseDefine.LOCAL_MQTT_URL,"String domain like 192.168.0.1 or example.com"),
        Port(1883.toString(),"Integer"),
        SSL(false.toString(),"Boolean"),
        UserName("root","userName to access"),
        Password("1234","password to access"),
        LoopInterval(3000L.toString(),"loop time (millisecond)"),
        LoopCount(1.toString(),"loop count (-1 = endless)")
    }
    enum class MySQL(
            val value:String,
            val explain:String
    ){
        Domain(BaseDefine.LOCAL_URL,"String domain like 192.168.0.1 or example.com"),
        Port(3306.toString(),"Integer"),
        UserName("choistec","userName to access"),
        Password("chois2016!","password to access"),
    }
    val mqttMap = MQTT.values().map { it.name to it.value }
            .toMap()
            .toMutableMap()

    val mysqlMap = MySQL.values().map { it.name to it.value }
            .toMap()
            .toMutableMap()

    private val help =
            "MQTT\n".plus(MQTT.values().joinToString { "--${it.name} : ${it.explain}\n" })
                    .plus("MySQL\n")
                    .plus(MySQL.values().joinToString { "--${it.name} : ${it.explain}\n" })
                    .plus("\n-yml : It ignores all command line options and uses the 'option.yml' file in same directory.\n")

    fun init(args:Array<String>) = try {
        args.filter {
            !it.startsWith("-yml").apply { load("option.yml",it) }
        }.map {
            it.split("=".toRegex()).takeIf { it[0].startsWith("--") }
                    ?: throw NoInitialContextException("입력 실패 $it\n$help")
        }.map {
            MQTT.valueOf(it[0].replace("--", ""))
        }.forEach {
            mqttMap[it.name] = it.value
        }
        true
    } catch (e: NoInitialContextException) {
        println(e)
        false
    } catch (e: IllegalArgumentException){
        println("$e 없는 옵션 \n$help")
        false
    }

    private fun load(path:String,option:String) :Boolean {
        try {
            try {
                Yaml().loadAll(File("""${System.getProperty("user.dir")}\$path""")
                        .readLines()
                        .joinToString("\n")
                )
            }catch (e:FileNotFoundException){
                Yaml().loadAll(File("""${System.getProperty("user.dir")}$path""")
                        .readLines()
                        .joinToString("\n")
                )
            }
                    .first()
                    .let { it as? LinkedHashMap<*, *>?:throw TypeCastException("YAML 파싱 오류") }
                    .let {data->
                        data["MQTT"]
                                ?.let { it as? LinkedHashMap<*, *>?:throw TypeCastException("카테고리 오류\n$it") }
                                ?.forEach {
                                    mqttMap[it.key.toString()]?.run {
                                        println("initialize option --${it.key}")
                                        mqttMap[it.key.toString()] = it.value.toString()
                                    }?:throw TypeCastException("없는 옵션 ${it.key}\n------you must use these properties------\n$help")
                                }?:println("카테고리 없음 : mqtt")
                        data["MySQL"]
                                ?.let { it as? LinkedHashMap<*, *>?:throw TypeCastException("카테고리 오류\n$it") }
                                ?.forEach {
                                    mysqlMap[it.key.toString()]?.run {
                                        println("initialize option --${it.key}")
                                        mysqlMap[it.key.toString()] = it.value.toString()
                                    }?:throw TypeCastException("없는 옵션 ${it.key}\n------you must use these properties------\n$help")
                                }?:println("카테고리 없음 : mysql")
                    }
            return true
        }catch (e:TypeCastException){
            println(e)
            return false
        }
    }
}
