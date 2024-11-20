package no.java.conf.service

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import io.github.oshai.kotlinlogging.KotlinLogging
import no.java.conf.model.ApiError
import no.java.conf.model.IndexNotReady
import no.java.conf.model.SearchMissing
import no.java.conf.model.search.SearchResponse
import no.java.conf.model.search.TextSearchRequest
import no.java.conf.model.search.VideoSearchResponse
import no.java.conf.model.sessions.Session
import no.java.conf.service.search.ElasticIndexer
import no.java.conf.service.search.ElasticIngester
import no.java.conf.service.search.ElasticSearcher
import kotlin.time.measureTimedValue

private const val INDEX_NAME = "javazone"

private val logger = KotlinLogging.logger {}

enum class State {
    NEW,
    MAPPED,
    INDEXED,
}

class SearchService(
    private val indexer: ElasticIndexer,
    private val ingester: ElasticIngester,
    private val searcher: ElasticSearcher,
    private val skipIndex: Boolean,
) {
    private var readyState = State.NEW

    suspend fun setup() {
        logger.debug { "Setting up SearchService" }

        if (readyState != State.NEW) {
            logger.debug { "Incorrect search state - was $readyState" }

            return
        }

        if (!skipIndex) {
            logger.debug { "Creating index" }

            indexer.recreateIndex(INDEX_NAME)
        }

        logger.debug { "State -> Mapped" }

        readyState = State.MAPPED
    }

    suspend fun ingest(sessions: List<Session>) {
        logger.debug { "Ingesting" }

        if (readyState != State.MAPPED) {
            logger.debug { "Incorrect search state - was $readyState" }

            return
        }

        if (!skipIndex) {
            logger.debug { "Bulk" }

            val timeTaken = measureTimedValue { ingester.ingest(INDEX_NAME, sessions) }

            logger.info { "Time taken to index - $timeTaken" }
        } else {
            logger.info { "Indexing skipped" }
        }

        logger.debug { "State -> Indexed" }

        readyState = State.INDEXED
    }

    context(Raise<ApiError>)
    suspend fun allVideos(): List<VideoSearchResponse> {
        ensureReady(readyState)

        return searcher.allVideos(INDEX_NAME)
    }

    fun state(): State = readyState

    context(Raise<ApiError>)
    suspend fun textSearch(searchRequest: TextSearchRequest?): SearchResponse {
        ensureNotNull(searchRequest) {
            raise(SearchMissing)
        }

        ensureReady(readyState)

        return searcher.textSearch(INDEX_NAME, searchRequest)
    }
}

private fun Raise<ApiError>.ensureReady(readyState: State) {
    ensure(readyState == State.INDEXED) {
        raise(IndexNotReady)
    }
}
