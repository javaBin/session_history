package no.java.conf

import com.jillesvangurp.ktsearch.SearchClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.java.conf.plugins.configureSearchRouting
import no.java.conf.service.SearchService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateTest {
    @Test
    fun testNewState() {
        val searchClient = mockk<SearchClient>()

        val service = SearchService(searchClient, false)

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
    fun testSkipIndex() {
        val searchClient = mockk<SearchClient>()

        val service = SearchService(searchClient, true)

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

        val service = SearchService(searchClient, true)

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

        val service = SearchService(searchClient, true)

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
