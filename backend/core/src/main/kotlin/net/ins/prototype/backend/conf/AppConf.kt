package net.ins.prototype.backend.conf

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AppProperties::class)
class AppConf
