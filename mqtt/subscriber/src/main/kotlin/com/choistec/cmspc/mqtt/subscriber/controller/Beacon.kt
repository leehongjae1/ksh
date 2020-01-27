package com.choistec.cmspc.mqtt.subscriber.controller

import com.choistec.cmspc.core.utility.CommonUtil.byteToFloat
import com.choistec.cmspc.core.utility.CommonUtil.toHex
import com.choistec.cmspc.core.utility.CommonUtil.use
import kotlin.experimental.and

enum class Beacon(
        //장비의 타입
        val code: Byte,
        //장비 데이터의 멤버 클래스 변수
        val types: Array<Type>
) {
    //체온, 호흡수
    TempBreath(0x01, arrayOf(Type.Temp, Type.Breath)),
    //산소포화도, 심박수
    Spo2Heart(0x02, arrayOf(Type.Spo2, Type.Heart)),
    //체중
    Weight(0x03, arrayOf(Type.Weight)),
    //수유량, 소변, 대변, 구토
    NurseUrineFecesVomit(0x04, arrayOf(Type.NurseIndex, Type.Nurse, Type.UrineIndex, Type.Urine, Type.FecesIndex, Type.Feces, Type.VomitIndex, Type.Vomit )),
    //이산화탄소, 온도, 습도
    Co2TempHumi(0x07, arrayOf(Type.Co2, Type.EnvTemp, Type.Humi)),
    //미세먼지 농도
    Dust(0x08, arrayOf(Type.Dust)),
    //전화번호
    Phone(0xff.toByte(), arrayOf(Type.Phone));

    enum class Type(
            //사이즈
            val size: Int,
            //최대 값
            val min: Double,
            //최소 값
            val max: Double,
            //계산
            val calc: (List<Byte>) -> String
    ) {
        //미세먼지
        Dust(4, 0.0, 300.0, {

            /*
             * 4 바이트를 16진수로 만들어서
             * 정수로 만들고 문자형으로 만든다.
             */

            it.take(4).toByteArray().toHex().toInt(16).toString()

        }),
        //이산화 탄소
        Co2(4, 0.0, 450.0, {

            /*
             * 4 바이트를 16 진수로 만들어서
             * 정수로 만들고 10 으로 나눠서
             * 문자형으로 만든다.
             */

            (it.take(4).toByteArray().toHex().toInt(16) / 10f).toString()
        }),
        //습도
        Humi(2, 0.0, 100.0, {

            /*
             * 2 바이트를 16 진수로 만들어서
             * 정수로 만들고 10 으로 나눠서
             * 문자형으로 만든다.
             */

            byteToFloat(it, 10f).toString()
        }),
        //온도 정수
        Temp(2, 28.00, 42.00, {
            //(36.00..38.00).random().round(2).toString()
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {

                /*
             * 2 바이트를 16 진수로 만들어서
             * 정수로 만들고 100 으로 나눠서
             * 문자형으로 만든다.
             */

                (it.take(2).toByteArray().toHex().toInt(16) / 100f).toString()
//                println("체온 첫번쨰 데이터 ${it[0]}")
//                println("체온 두번쨰 데이터 ${it[1]}")
                //byteToFloat(it, 100f).toString()
            }
        }),
        NurseIndex(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        Nurse(2, 0.00, 1000.00, {
            //(36.00..38.00).random().round(2).toString()
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(2).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        UrineIndex(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        Urine(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        FecesIndex(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        Feces(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        VomitIndex(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        Vomit(1, 0.00, 100.00, {
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {
                (it.take(1).toByteArray().toHex().toInt(16)).toString()
            }
        }),
        //온도 소수점
        EnvTemp(2, 28.00, 42.00, {
            //(36.00..38.00).random().round(2).toString()
            if (it[0] == 0xff.toByte() && it[1] == 0xff.toByte()) {
                "-1.255"
            } else {

                /*
             * 2 바이트를 16 진수로 만들어서
             * 정수로 만들고 100 으로 나눠서
             * 문자형으로 만든다.
             */

                //(it.take(2).toByteArray().toHex().toInt(16) / 100f).toString()
                byteToFloat(it, 100f).toString()
            }
        }),
        //심박수
        Heart(2, 60.0, 120.0, {

            /*
             * 2 바이트를 16 진수로 만들어서
             * 정수로 만들고 10 으로 나눠서
             * 문자형으로 만든다.
             */

            (it.take(2).toByteArray().toHex().toInt(16) / 10f).toString()
        }),
        //호흡수
        Breath(1, 10.0, 50.0, {
            it[0].toInt().toString()
        }),
        //산소포화도
        Spo2(2, 50.0, 200.0, {
            (it.take(2).toByteArray().toHex().toInt(16) / 10f).toString()
        }),
        //체중
        Weight(4, 50.0, 200.0, {
            ((it.take(4).toByteArray().toHex().toInt(16))/100f).toString()
        }),
        //전화번호
        Phone(2, 20.0, 0.0, {
            String(it.filter { it != 0x00.toByte() }.toByteArray())
        }),
        None(2, 0.0, 0.0, {
            ""
        })
    }

    data class Data(
            val beacon: Beacon,
            //맥주소
            var mac: String? = null,
            //아이디
            var id: Int? = null,
            //배터리
            var battery: Int? = null,
            var prefix: String? = null,
            /*
             * 맥주소 아이디 배터리를 제외한 그
             * 다음 바이트
             */
            var bytes: List<Byte> = emptyList()
    ) {
        val values by lazy {
            beacon.types.map {
                var value = ""
                //테스트 - 특정 아이디 - 이름 바이트 로그
                //if(id==90003){
//                    println("id $id")
//                    println("byteToInt ${it.name} ${bytes[1].toInt()}")
//                    println("byte ${it.name} ${bytes}")
                //}

                bytes = bytes.use(it.size) { value = it.calc(bytes) }
                value
            }
        }
    }

    /**
     * 바이트를 나눠서 원하는 정보 변수에 담는다.
     */
    fun parse(data: List<Byte>) = Data(this).apply {
        //6바이트를 맥주수로 추출
        data.use(6) { mac = toByteArray().toHex(":") }
                //그 다음 1바이트를 타입 바이트로 추출
                .use(1) {
                    prefix ="${get(0)}"
                    /*prefix code*/}
                //그 다음 5바이트를 아이디 값으로 추출
                .use(5) { id = "${get(0)}${get(1)}${get(2)}${get(3)}${get(4)}".toInt() }
                //그 다음 1 바이트를 배터리 값으로 추출
                .use(1) { battery = (get(0) and 0xffff.toByte()).toInt() }
                //그 다음 바이트를 데이터 값으로 추출
                .use(13) {
                    //테스트 - 특정 아이디 - 바이트 나눌 때의 로그 검사
                    if(id==61011 && prefix=="4"){
                    println("mac : $mac")
                    println("prefix : $prefix")
                    println("id : $id")
                    println("battery : $battery")
                    println("bytes : $this")
                }

                    bytes = this
                }
    }

    companion object {

        //바이트를 나눠서 원하는 정보 변수에 담는다.
        fun parse(data: List<Byte>) = Beacon[data[6]]?.parse(data)

        @JvmStatic
        fun parseRawData(data: ByteArray) =
                /*
                * take : 리스트 앞에서 take 앞의
                * 숫자만큼 자르는 함수
                */
                data.take(6).toByteArray().toHex(":") to
                        /*
                        * drop : take 에서 받은 리스트를 버리고 drop 에서
                        * 설정한 숫자만큼 나머지를 가져오는 함수
                        */
                        data.drop(6)
                                .withIndex()
                                /*
                                * groupBy : 리스트를 특정 기준으로
                                * 분류하기 위해 쓰는 함수
                                */
                                .groupBy { it.index / 26 }
                                /*
                                * map : 기존의 Observalble 로 부터 받아들인 데이터를
                                * 새로운 형태로 변형하는데 쓰이는 것
                                */
                                .map { it.value.map { it.value } }
                                .map {
                                    //테스트 - 바이트 - 순수 오리지날 바이트 로그
                                    //println(it)
                                    parse(it)
                                }

        @JvmStatic
        operator fun get(code: Byte) = Beacon.values().firstOrNull { it.code == code }
    }
}
