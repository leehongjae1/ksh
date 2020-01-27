package com.choistec.cmspc.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.service.Contact

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api() = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(ApiInfoBuilder()
                    .title("Smart Cradle")
                    .description("smart cradle api")
                    .contact(Contact("Choistechnology", "www.choistec.com", "rnd4@choistec.com"))
                    .build())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.choistec.cmspc.api.controller"))
            .paths(PathSelectors.any())
            .build()
    //swagger-ui.html
}
