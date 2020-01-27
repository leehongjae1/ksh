package com.choistec.cmspc.api.config

import com.choistec.cmspc.core.mysql.dao.*
import com.choistec.cmspc.core.mysql.option.DatabaseOption
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@Lazy
@EnableTransactionManagement
@MapperScan(basePackages = ["com.choistec.cmspc.core.mysql.dao"])
class DatabaseConfig{
    @Autowired
    private lateinit var config : YAMLConfig

    @Bean(destroyMethod = "close")
    fun dataSource(): DataSource  =
            DatabaseOption.Local.configureDataSource(org.apache.tomcat.jdbc.pool.DataSource()).apply {
                config.database.let {

                    //테스트 - 데이터베이스 - API 연결 설정 값 확인
//                    println("API 설정 값 확인")
//                    println("driverClassName ${driverClassName}")
//                    println("url ${url}")
//                    println("username ${username}")
//                    println("password ${password}")
//                    println("maxActive ${maxActive}")
//                    println("initialSize ${initialSize}")
//                    println("maxIdle ${maxIdle}")
//                    println("minIdle ${minIdle}")
//                    println("maxWait ${maxWait}")
//                    println("validationQuery ${validationQuery}")
//                    println("isTestWhileIdle ${isTestWhileIdle}")
//                    println("timeBetweenEvictionRunsMillis ${timeBetweenEvictionRunsMillis}")
//                    println("isTestOnBorrow ${isTestOnBorrow}")
//                    println("isTestOnReturn ${isTestOnReturn}")

                    url = "jdbc:mysql://${it.host}:${it.port}/cms_pc_db?allowMultiQueries=true"
                    username = it.user
                    password = it.password
                    validationQuery="SELECT 1"
                    isTestWhileIdle = true
                    timeBetweenEvictionRunsMillis = 7200000
                }
            }

    @Bean
    fun sqlSessionFactory(): SqlSessionFactory? = SqlSessionFactoryBean()
            .apply { setDataSource(dataSource()) }
            .`object`

    private final inline fun <reified T> getDao() = SqlSessionTemplate(sqlSessionFactory())
            .getMapper(T::class.java)

    @Bean
    fun adminDao() = getDao<AdminDAO>()
    @Bean
    fun rearerDao() = getDao<RearerDAO>()
    @Bean
    fun babyDao() = getDao<PatientDAO>()
    @Bean
    fun measureDao() = getDao<MeasureDAO>()
    @Bean
    fun growthDao() = getDao<GrowthDAO>()
}
