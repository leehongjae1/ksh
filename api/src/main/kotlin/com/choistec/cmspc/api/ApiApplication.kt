package com.choistec.cmspc.api

import com.choistec.cmspc.api.config.YAMLConfig
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}

@SpringBootApplication
@EnableAutoConfiguration(exclude = [
    DataSourceTransactionManagerAutoConfiguration::class,
    DataSourceAutoConfiguration::class,
    MybatisAutoConfiguration::class]
)

class ApiApplication : CommandLineRunner {
    @Autowired
    private lateinit var config : YAMLConfig
    override fun run(vararg args :String){
        println("using environment: " + config.environment)
        println("name: " +config.name)
    }
}
