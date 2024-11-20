package no.java.conf

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
import no.java.conf.service.search.ElasticSearcher
import kotlin.test.Test
import kotlin.test.assertEquals

class StateTest {
    @Test
    fun testNew() {
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, false)

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
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, false)

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
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, false)

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
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, true)

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
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, true)

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
        val indexer = mockk<ElasticIndexer>()
        val ingester = mockk<ElasticIngester>()
        val searcher = mockk<ElasticSearcher>()

        val service = SearchService(indexer, ingester, searcher, true)

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
