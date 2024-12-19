package no.java.conf

import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig
import no.java.conf.config.EndpointConfig
import no.java.conf.config.LocalFileConfig
import no.java.conf.config.SearchClientConfig
import no.java.conf.config.SearchServiceConfig
import no.java.conf.service.search.ElasticIndexer
import no.java.conf.service.search.ElasticIngester
import no.java.conf.service.search.ElasticSearcher

fun ApplicationConfig.str(key: String) = property(key).getString()

fun ApplicationConfig.int(key: String) = str(key).toInt()

fun ApplicationConfig.bool(key: String) = str(key).toBoolean()

fun ApplicationEnvironment.str(key: String) = config.str(key)

fun ApplicationEnvironment.int(key: String) = config.int(key)

fun ApplicationEnvironment.bool(key: String) = config.bool(key)

fun ApplicationEnvironment.searchClientConfig() =
    SearchClientConfig(
        host = str("elastic.host"),
        port = int("elastic.port"),
        username = str("elastic.username"),
        password = str("elastic.password"),
    )

fun ApplicationEnvironment.searchServiceConfig(searchClient: SearchClient) =
    SearchServiceConfig(
        indexer = ElasticIndexer(searchClient),
        ingester = ElasticIngester(searchClient),
        searcher = ElasticSearcher(searchClient),
        skipIndex = bool("elastic.skipindex")
    )

fun ApplicationEnvironment.endpointConfig(key: String) =
    config.configList(key).map {
        EndpointConfig(it.int("year"), it.str("endpoint"))
    }

fun ApplicationEnvironment.localFileConfig(key: String) =
    config.configList(key).map {
        LocalFileConfig(it.int("year"), it.str("LocalFileConfig"))
    }
