package pt.br.mok

import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.Parameters

data class ServiceConfig(val responses: Map<String, List<Response>> = emptyMap()) {
    companion object {
        fun from(config: Config) = ServiceConfig(config.responses.groupBy { it.match.path })
    }

    fun findResponse(path: String, params: Parameters, headers: Headers) =
        responses[path]?.firstOrNull { response ->
            response.match.headerMap.all { entry -> entry.value.any { headers.contains(entry.key, it) } } &&
                    response.match.queryMap.all { entry -> entry.value.any { params.contains(entry.key, it) } }
        }
}

data class Config(val responses: List<Response>)

data class Response(
    val match: Match = Match(),
    val contentType: String = "text/plain",
    private val template: String = ""
) {
    val properContentType: ContentType
        get() = ContentType.parse(contentType)

    fun body(params: Parameters, headers: Headers): String {
        return (headers.entries().asSequence().map { e -> "##H#${e.key}##" to e.value.first() } +
                params.entries().asSequence().map { e -> "##Q#${e.key}##" to e.value.first() })
            .fold(template) { tmpl, repl -> tmpl.replace(repl.first, repl.second) }
    }
}

data class Match(
    val path: String = "",
    val query: Map<String, String> = emptyMap(),
    private val headers: Map<String, String> = emptyMap()
) {

    val queryMap: Map<String, List<String>>
        get() = query.map { it.key to it.value.split(",") }.toMap()

    val headerMap: Map<String, List<String>>
        get() = headers.map { it.key to it.value.split(",") }.toMap()
}