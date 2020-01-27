package com.choistec.cmspc.mqtt.subscriber.config

import com.choistec.cmspc.core.mysql.dao.MeasureDAO
import com.choistec.cmspc.core.mysql.dao.ScannerDAO
import com.choistec.cmspc.core.mysql.option.DatabaseOption
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import org.apache.tomcat.jdbc.pool.DataSource

object DatabaseConfig {
	val option = DatabaseOption.Local.configureDataSource(DataSource())
	val sqlSessionFactory: SqlSessionFactory by lazy {
		SqlSessionFactoryBuilder()
				.build(Configuration(
						Environment.Builder("CmsPc").apply {
							transactionFactory(JdbcTransactionFactory())

							//테스트 - 데이터베이스 - Subscriber 셋팅 값 로그 검사
//							println("subscriber 설정 값 확인")
//							println("driverClassName ${option.driverClassName}")
//							println("url ${option.url}")
//							println("username ${option.username}")
//							println("password ${option.password}")
//							println("maxActive ${option.maxActive}")
//							println("initialSize ${option.initialSize}")
//							println("maxIdle ${option.maxIdle}")
//							println("minIdle ${option.minIdle}")
//							println("maxWait ${option.maxWait}")
//							println("validationQuery ${option.validationQuery}")
//							println("isTestWhileIdle ${option.isTestWhileIdle}")
//							println("timeBetweenEvictionRunsMillis ${option.timeBetweenEvictionRunsMillis}")
//							println("isTestOnBorrow ${option.isTestOnBorrow}")
//							println("isTestOnReturn ${option.isTestOnReturn}")

							dataSource(option)
						}.build())
						.apply {
							addMapper(ScannerDAO::class.java)
							addMapper(MeasureDAO::class.java)
						})

	}

	var session: SqlSession? = null

	inline fun <reified T> getDao(): T = (session ?: sqlSessionFactory.openSession().also { session = it })
			.getMapper(T::class.java)
}
