package no.java.conf

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            client.get("/").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertTrue { bodyAsText().contains("Hello Ktor!") }
            }
        }
}
