package net.ins.prototype.backend.conf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConf {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer = object : WebMvcConfigurer {

        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/v1/profiles/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET")
                .allowCredentials(true)
                .allowedHeaders("*")
        }
    }
}
