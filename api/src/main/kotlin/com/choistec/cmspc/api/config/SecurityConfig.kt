package com.choistec.cmspc.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig: WebSecurityConfigurerAdapter(){

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().requireCsrfProtectionMatcher(AntPathRequestMatcher("**/login")).and()
                .authorizeRequests().antMatchers("/dashboard").hasRole("USER").and()
                .formLogin().defaultSuccessUrl("/dashboard").loginPage("/login").and()
                .logout().permitAll()
    }

    @Bean
    fun bCryptPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/*.css")
        web.ignoring().antMatchers("/*.js")
    }
}
