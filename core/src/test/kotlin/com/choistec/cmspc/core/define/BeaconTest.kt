package com.choistec.cmspc.core.define

import com.choistec.cmspc.core.utility.CommonUtil.hexToByteArray
import org.junit.Assert.assertEquals
import org.junit.Test

class BeaconTest{
    @Test
    fun temp(){
        val (mac,data) = Beacon.parseRawData("a1,b2,c3,d4,e5,f6,,ab,cd,11,22,33,44,01,09,00,00,00,01,64,,FF,FF,0E,D1,49,64,00,00,00,00,00,00,00".hexToByteArray(","))
        assertEquals(mac,"a1:b2:c3:d4:e5:f6")
        data.filter { it?.beacon == Beacon.TempBreath }
                .forEach {it?.let {
                    assertEquals(it.beacon,Beacon.TempBreath)
                    it.values.forEach(::println)
                    assertEquals(it.values[0],"-1.255")
                    assertEquals(it.battery,100)
                    assertEquals(it.id,90001)
                    assertEquals(it.mac,"ab:cd:11:22:33:44")
                }}


    }
    @Test
fun temp2(){

        println("testtest")
    }
}
