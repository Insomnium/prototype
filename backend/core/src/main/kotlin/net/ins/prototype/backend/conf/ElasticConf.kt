package net.ins.prototype.backend.conf

import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.support.HttpHeaders

//@Configuration
class ElasticConf(
    private val elasticsearchProperties: ElasticsearchProperties,
) : ElasticsearchConfiguration() {

    override fun clientConfiguration(): ClientConfiguration {
        val headers = HttpHeaders()

        headers.add("Accept", "application/vnd.elasticsearch+json;compatible-with=7");
        headers.add("Content-Type", "application/vnd.elasticsearch+json;compatible-with=7");

        return ClientConfiguration.builder()
            .connectedTo(elasticsearchProperties.uris[0])
            .withDefaultHeaders(headers)
            .build();
    }
}
