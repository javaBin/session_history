package no.java.conf

import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import no.java.conf.plugins.configureSearchRouting
import no.java.conf.service.SearchService
import no.java.conf.service.search.ElasticIndexer
import no.java.conf.service.search.ElasticIngester
import kotlin.test.Test
import kotlin.test.assertEquals

class StateTest {
    @Test
    fun testNew() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, false)

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "NEW")
            }
        }
    }

    @Test
    fun testMapped() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, false)

        coEvery { indexer.recreateIndex(any()) } just runs

        runBlocking {
            service.setup()
        }

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "MAPPED")
            }
        }
    }

    @Test
    fun testIndexed() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, false)

        coEvery { indexer.recreateIndex(any()) } just runs
        coEvery { ingester.ingest(any(), any()) } just runs

        runBlocking {
            service.setup()
            service.ingest(emptyList())
        }

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "INDEXED")
            }
        }
    }

    @Test
    fun testSkipIndexNew() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, true)

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "NEW")
            }
        }
    }

    @Test
    fun testSkipIndexMapped() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, true)

        runBlocking {
            service.setup()
        }

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "MAPPED")
            }
        }
    }

    @Test
    fun testSkipIndexIndexed() {
        val searchClient = mockk<SearchClient>()
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()

        val service = SearchService(searchClient, indexer, ingester, true)

        runBlocking {
            service.setup()
            service.ingest(emptyList())
        }

        testApplication {
            application {
                configureSearchRouting(service)
            }

            client.get("/api/search/state").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(bodyAsText(), "INDEXED")
            }
        }
    }
}
