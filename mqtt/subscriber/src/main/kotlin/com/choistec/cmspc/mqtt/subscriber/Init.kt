package com.choistec.cmspc.mqtt.subscriber

import com.choistec.cmspc.core.option.BaseOption
import com.choistec.cmspc.core.option.BaseOption.MQTT
import com.choistec.cmspc.core.option.BaseOption.MySQL
import com.choistec.cmspc.mqtt.model.MqttModel
import com.choistec.cmspc.mqtt.subscriber.config.DatabaseConfig
import com.choistec.cmspc.mqtt.subscriber.controller.DATA_SAVE_INTERVAL
import com.choistec.cmspc.mqtt.subscriber.controller.DataController
import com.choistec.cmspc.mqtt.subscriber.controller.MqttSubscriber
import org.apache.commons.daemon.Daemon
import org.apache.commons.daemon.DaemonContext
import org.eclipse.paho.client.mqttv3.MqttException
import java.util.*

val baseOption = BaseOption()
fun main(args: Array<String>) {
	if(baseOption.init(args)) {
		DatabaseConfig.option.run {
			url = "jdbc:mysql://${baseOption.mysqlMap[MySQL.Domain.name]?:MQTT.Domain.value}:" +
					"${baseOption.mysqlMap[MySQL.Port.name]?:MQTT.Port.value}/cms_pc_db?allowMultiQueries=true"

			username = baseOption.mysqlMap[MySQL.UserName.name]?:MQTT.UserName.value
			password = baseOption.mysqlMap[MySQL.Password.name]?:MQTT.Password.value
			//테스트 - 데이터베이스 - Subscriber 셋팅 값 로그 검사
			validationQuery = "select 1"
			isTestWhileIdle = true
			timeBetweenEvictionRunsMillis = 7200000
			println("Subscriber 설정 값 확인")
			println("driverClassName ${driverClassName}")
			println("url ${url}")
			println("username ${username}")
			println("password ${password}")
			println("maxActive ${maxActive}")
			println("initialSize ${initialSize}")
			println("maxIdle ${maxIdle}")
			println("minIdle ${minIdle}")
			println("maxWait ${maxWait}")
			println("validationQuery ${validationQuery}")
			println("isTestWhileIdle ${isTestWhileIdle}")
			println("timeBetweenEvictionRunsMillis ${timeBetweenEvictionRunsMillis}")
			println("isTestOnBorrow ${isTestOnBorrow}")
			println("isTestOnReturn ${isTestOnReturn}")
		}
		Init.start()
	}
}

fun stop(args: Array<String>) {
	Init.stop()
}

object Init:Daemon{
    private var serviceThread:Thread? = null
	private val controller : MqttModel.Subscribe by lazy{
        MqttSubscriber(MqttModel(baseOption.mqttMap[MQTT.Domain.name]?:MQTT.Domain.value).apply {
            quietMode = baseOption.mqttMap[MQTT.LogLevel.name]?:MQTT.LogLevel.value.toInt()==2
            ssl = (baseOption.mqttMap[MQTT.SSL.name]?:MQTT.SSL.value).toBoolean()
            port = (baseOption.mqttMap[MQTT.Port.name]?:MQTT.Port.value).toInt()
            userName = baseOption.mqttMap[MQTT.UserName.name]?:MQTT.UserName.value
            password = baseOption.mqttMap[MQTT.Password.name]?:MQTT.Password.value
	}).mqttAdapter {
        DataController.Topics[it.first]?.run { process(it.second) }
	}}

	private val service = Runnable{
		println("service init")
		try{
            controller.connect()
            controller.subscribe(DataController.Topics.BaseTopic.url)

			while(!Thread.currentThread().isInterrupted){

				DataController.dataSave()
				//DataController.envClean()
				Thread.sleep(DATA_SAVE_INTERVAL *1000L)
				//Thread.sleep(baseOption.mqttMap[MQTT.LoopInterval.name].toRealTimeQuery().toLong())
			}
		}catch(e:InterruptedException){
            println("interrupt")
			//destroy()
        }catch (e: MqttException){
            println(Date())
            println("reconnecting....")
            println(e)
            Thread.sleep(3 *1000L)
            start()
        }
	}

	override fun init(arg0:DaemonContext){
		println("linux")
	}

	override fun start(){
        serviceThread?.interrupt()
        serviceThread = null
		serviceThread = Thread(service).apply{start()}
	}

	override fun stop(){
        serviceThread?.interrupt()
	}

	override fun destroy(){
		serviceThread = null
		controller.disconnect()
	}
}
