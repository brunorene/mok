package pt.br.mok

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import java.io.File

fun main(args: Array<String>) {
    val config = processYamlConfig(if (args.isEmpty()) File(".") else File(args[0]))
    embeddedServer(Netty, 8080) {
        routing {
            route("{path...}") {
                get {
                    val waitFor = call.request.header("MOK-DELAY-MS")?.toLong()
                    if (waitFor != null)
                        delay(waitFor)
                    val response = config.findResponse(call.request.path(), call.parameters, call.request.headers)
                    if (response == null)
                        call.respond(HttpStatusCode.NotFound, "")
                    else
                        call.respondText(
                            response.body(call.parameters, call.request.headers),
                            response.properContentType
                        )
                }
            }
        }
    }.start(wait = true)
}

fun processYamlConfig(configFile: File): ServiceConfig {
    val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    if (configFile.isDirectory) {
        return ServiceConfig.from(configFile
            .listFiles { file -> file.name.endsWith(".yml") || file.name.endsWith(".yaml") }
            .map { mapper.readValue<Config>(it) }
            .reduce { acc, config -> acc.copy(responses = acc.responses + config.responses) })
    }
    if (configFile.isFile) {
        return ServiceConfig.from(mapper.readValue(configFile))
    }
    return ServiceConfig()
}