package no.java.conf.config

import no.java.conf.service.search.ElasticIndexer
import no.java.conf.service.search.ElasticIngester
import no.java.conf.service.search.ElasticSearcher

data class SearchServiceConfig(
    val indexer: ElasticIndexer,
    val ingester: ElasticIngester,
    val searcher: ElasticSearcher,
    val skipIndex: Boolean,
)
