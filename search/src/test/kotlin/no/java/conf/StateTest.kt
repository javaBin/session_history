package no.java.conf

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
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
import kotlin.test.assertEquals

class StateTest :
    FunSpec({
        test("test new") {
            testApplication {
                buildTestApplication(skipIndex = false)

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "NEW")
                }
            }
        }

        test("test mapped") {
            val indexer = mockk<ElasticIndexer>()

            coEvery { indexer.recreateIndex(any()) } just runs

            testApplication {
                buildTestApplication(indexer = indexer, skipIndex = false) { service ->
                    service.setup()
                }

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "MAPPED")
                }
            }
        }

        test("test indexed") {
            val indexer = mockk<ElasticIndexer>()
            val ingester = mockk<ElasticIngester>()

            coEvery { indexer.recreateIndex(any()) } just runs
            coEvery { ingester.ingest(any(), any()) } just runs

            testApplication {
                buildTestApplication(indexer = indexer, ingester = ingester, skipIndex = false) { service ->
                    service.setup()
                    service.ingest(emptyList())
                }

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "INDEXED")
                }
            }
        }

        test("test skip index new") {
            testApplication {
                buildTestApplication(skipIndex = true)

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "NEW")
                }
            }
        }

        test("test skip index mapped") {
            val indexer = mockk<ElasticIndexer>()

            coEvery { indexer.recreateIndex(any()) } just runs

            testApplication {
                buildTestApplication(indexer = indexer, skipIndex = false) { service ->
                    service.setup()
                }

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "MAPPED")
                }
            }
        }

        test("test skip index indexed") {
            val indexer = mockk<ElasticIndexer>()
            val ingester = mockk<ElasticIngester>()

            coEvery { indexer.recreateIndex(any()) } just runs
            coEvery { ingester.ingest(any(), any()) } just runs

            testApplication {
                buildTestApplication(indexer = indexer, ingester = ingester, skipIndex = false) { service ->
                    service.setup()
                    service.ingest(emptyList())
                }

                client.get("/api/search/state").apply {
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals(bodyAsText(), "INDEXED")
                }
            }
        }
    })

private fun ApplicationTestBuilder.buildTestApplication(
    indexer: ElasticIndexer = mockk(),
    ingester: ElasticIngester = mockk(),
    searcher: ElasticSearcher = mockk(),
    skipIndex: Boolean = false,
    block: (suspend (service: SearchService) -> Unit)? = null,
) {
    val service =
        SearchService(
            indexer = indexer,
            ingester = ingester,
            searcher = searcher,
            skipIndex = skipIndex
        )

    runBlocking {
        block?.invoke(service)
    }

    application {
        configureSearchRouting(service)
    }
}
