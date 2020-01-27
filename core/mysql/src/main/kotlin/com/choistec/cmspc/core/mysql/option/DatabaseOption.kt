package com.choistec.cmspc.core.mysql.option

import com.choistec.cmspc.core.define.BaseDefine

enum class DatabaseOption (
        private val driverClassName: String,
        val url: String,
        private val userName: String,
        private val password: String,
        private val initialSize: Int,
        private val maxActive: Int,
        private val maxIdle: Int,
        private val minIdle: Int,
        private val maxWait: Int
){
    Local(
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://${BaseDefine.LOCAL_URL}:3306/cms_pc_db?allowMultiQueries=true",
            "choistec",
            "chois2016!",
            10,
            10,
            10,
            10,
            3000
    ),
    Amazon(
            "com.mysql.jdbc.Driver",
            "jdbc:mysql://${BaseDefine.AMAZON_URL}:3306/cms_pc_db?allowMultiQueries=true",
            "choistec",
            "chois2016!",
            10,
            10,
            10,
            10,
            3000
    );

    fun configureDataSource(dataSource: org.apache.tomcat.jdbc.pool.DataSource) =
            dataSource.also {
                it.driverClassName = driverClassName
                it.url = url
                it.username = userName
                it.password = password
                it.maxActive = maxActive
                it.initialSize = initialSize
                it.maxIdle = maxIdle
                it.minIdle = minIdle
                it.maxWait = maxWait
                it.validationQuery = "select 1"
                it.isTestWhileIdle = true
                it.timeBetweenEvictionRunsMillis = 7200000
                it.isTestOnBorrow = false
                it.isTestOnReturn = false
            }
}
