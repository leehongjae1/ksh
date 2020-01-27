package com.choistec.cmspc.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
class YAMLConfig {
    var name: String? = null
    var environment: String? = null
    var database : Database = Database()
}

class Database{
    lateinit var host : String
    var port : Int = 3306
    lateinit var user : String
    lateinit var password : String
}
