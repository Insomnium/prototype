package net.ins.prototype.backend.conf

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConf {

    @Bean
    fun minioClient(
        appProperties: AppProperties,
    ): MinioClient = MinioClient.builder()
        .endpoint(appProperties.objectStorage.connectionUrl)
        .credentials(appProperties.objectStorage.user, appProperties.objectStorage.password)
        .build()
}