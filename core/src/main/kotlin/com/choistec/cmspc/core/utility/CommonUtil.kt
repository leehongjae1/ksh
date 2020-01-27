package com.choistec.cmspc.core.utility

import java.util.*
import kotlin.experimental.and
import kotlin.math.pow

object CommonUtil {
	fun ClosedRange<Int>.random() =
			Random().nextInt(endInclusive + 1 - start) +  start
	fun ClosedFloatingPointRange<Double>.random() =
			Random().nextDouble()*(endInclusive - start) +  start
	fun Double.round(digit:Int) =
			Math.round(this*10.0.pow(digit))/10.0.pow(digit)


	fun <T> List<T>.use(separator:Int, block: List<T>.() -> Unit):List<T> {
		take(separator).block()
		return this.drop(separator)
	}

	fun byteToFloat(byteArray: List<Byte>, devideValue:Float): Float {
		val intArray = IntArray(2)
		intArray[0] = byteArray[0].toInt()
		/**
		 * 두번 째 배열이 만약 0 보다 작을 경우
		 * 256 을 더해준다.
		 */
		if (byteArray[1] < 0) {
			intArray[1] = byteArray[1].toInt() + 256
		} else {
			/** 0보다 크면 그대로 값을 담는다. */
			intArray[1] = byteArray[1].toInt()
		}


		return ((intArray[0] * 256) + intArray[1]) / devideValue
	}

	fun ByteArray.toHex(
			separator:String = ""
    ) = joinToString(separator) { String.format("%02x", it and 0xff.toByte()) }

	fun String.hexToByteArray(separator:String = "") = replace(separator,"")
			.withIndex()
			.groupBy { it.index/2 }
			.map {
				try {
					((Character.digit(it.value[0].value, 16) shl 4) + Character.digit(it.value[1].value, 16)).toByte()
				}catch (e:StringIndexOutOfBoundsException){
					println("잘못된 헥사코드 - $this\n반드시 짝수의 문자를 입력할 것")
					e.printStackTrace()
					0x00.toByte()
				}
			}
			.toByteArray()
}
