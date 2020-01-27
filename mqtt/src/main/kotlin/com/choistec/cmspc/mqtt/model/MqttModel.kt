package com.choistec.cmspc.mqtt.model

open class MqttModel(
        val domain:String
){
    var quietMode = false
    var ssl = false
    var port = 1883
    var url =""
        get(){
            return "${(if(ssl)"ssl" else "tcp")}://$domain:$port"
        }
    var userName:String? = null
    var password:String? = null
    object ClientNullException:Exception("Make sure your MQTT client did initialize")
    interface MqttAdapter{
        var qos :Int
        fun connect()
        fun disconnect()
    }
    abstract class Subscribe: MqttAdapter {
        override var qos = 2
            set(value){
                if (value in 0..2) field = value else{ throw Exception("Invalid QoS: $value") }
            }
        fun subscribe(vararg topics: String) = topics.forEach { subscribe(it,qos) }
        /**
         * Subscribe to a topic on an MQTT server
         * Once subscribed this method waits for the messages to arrive from the server
         * that match the subscription. It continues listening for messages until the enter key is
         * pressed.
         * @param topic to Subscribe to (can be wild carded)
         * @param qos the maximum quality of service to receive messages at for this subscription
         */
        abstract fun subscribe(topic:String,qos:Int)
    }
    abstract class Publish: MqttAdapter {
        override var qos = 2
            set(value){
                if (value in 0..2) field = value else{ throw Exception("Invalid QoS: $value") }
            }
        fun publish(vararg topics: Pair<String,ByteArray>) = topics.forEach {publish(it.first,qos,it.second) }

        /**
         * Publish / send a message to an MQTT server
         * @param topic the name of the topic to Publish to
         * @param qos the quality of service to delivery the message at (0,1,2)
         * @param payload the set of bytes to send to the MQTT server
         */
        abstract fun publish(topic:String,qos:Int,payload:ByteArray)
    }
}
