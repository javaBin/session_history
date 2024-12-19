package no.java.conf.config

data class SearchClientConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
) {
    override fun toString(): String = "Config: $host:$port $username"
}
