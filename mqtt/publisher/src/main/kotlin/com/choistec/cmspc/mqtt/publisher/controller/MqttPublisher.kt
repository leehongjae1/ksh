package com.choistec.cmspc.mqtt.publisher.controller

import com.choistec.cmspc.core.utility.CommonUtil.toHex
import com.choistec.cmspc.mqtt.model.MqttModel
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import java.sql.Timestamp

class MqttPublisher @Throws(MqttException::class) constructor(val option: MqttModel){
	private var client : MqttClient? = null
	private var conOpt:MqttConnectOptions = MqttConnectOptions().apply {
		isCleanSession = false
		option.password?.run{ password = toCharArray() }
		option.userName?.run{ userName = this }
	}

	fun  mqttAdapter(onMessageArrived:(Pair<String,String>)->Unit) :MqttModel.Publish {
		client?.disconnect()
		client = MqttClient(
				option.url,
				"SMART_CRADLE_MQTT_PUB",
				MqttDefaultFilePersistence(System.getProperty("java.io.tmpdir"))
		).apply{
			setCallback(object : MqttCallback{
			override fun deliveryComplete(token: IMqttDeliveryToken?) {}

			@Throws(MqttException::class)
			override fun messageArrived(topic: String, message: MqttMessage) {
				onMessageArrived(topic to String(message.payload))
			}

			override fun connectionLost(cause: Throwable?) {
				println("Connection to ${option.url} lost! $cause")
				System.exit(1)
			}
		})}
		return object : MqttModel.Publish() {
			@Throws(MqttException::class)
			override fun connect() {
				println("Connecting to ${option.url} with client ID ${client?.clientId}")
				client?.connect(conOpt) ?: throw MqttModel.ClientNullException
				println("Connected")
			}

            @Throws(MqttException::class)
            override fun publish(topic: String, qos: Int, payload: ByteArray) {
				//테스트 - 퍼블리쉬 - 토픽과 데이터 확인 로그
                println("Publishing at: ${Timestamp(System.currentTimeMillis())} to topic \"$topic\"" +
						"  ${payload.toHex(" ")} qos $qos")
                client?.publish(topic, MqttMessage(payload).also{it.qos = qos})
            }
			/*
			@Throws(MqttException::class)
			override fun subscribe(topic: String, qos: Int) {
				println("""Subscribing to topic "$topic" qos $qos""")
				client?.subscribe(topic, qos)
			}
			*/
			@Throws(MqttException::class)
			override fun disconnect() = client?.disconnect()
					.also { println("Disconnected") } ?: throw MqttModel.ClientNullException
		}
	}

		/**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
	private fun println(message:String){
		if(!option.quietMode) System.out.println(message)
	}
}
