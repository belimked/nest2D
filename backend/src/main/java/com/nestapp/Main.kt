package com.nestapp

import com.nestapp.nest_api.NestedRepository
import com.nestapp.nest_api.UserInputExecution
import com.nestapp.nest_api.nestRestApi
import com.nestapp.projects.ProjectsRepository
import com.nestapp.projects.projectRest
import io.ktor.http.HttpMethod.Companion.DefaultMethods
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import java.io.File

internal object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        val projectsRepository = ProjectsRepository()
        val nestedRepository = NestedRepository(json)

        embeddedServer(CIO, port = 8080) {
            applicationEngineEnvironment {
                developmentMode = true
            }
            install(StatusPages) {
                exception<UserInputExecution> { call, userInputExecution ->
                    println(userInputExecution.printStackTrace())
                    call.respond(HttpStatusCode.BadRequest, userInputExecution.getBody())
                }
                exception<Throwable> { cause, throwable ->
                    println(throwable.printStackTrace())
                    cause.respond(HttpStatusCode.InternalServerError, "Error: $throwable")
                }
            }

            install(AutoHeadResponse)

            install(CORS) {
                anyHost()
                allowHeaders { true }
                allowCredentials = true
                DefaultMethods.forEach(::allowMethod)

                allowNonSimpleContentTypes = true
            }

            install(ContentNegotiation) {
                json()
            }

            routing {
                route("/api") {
                    projectRest(File("mount"), projectsRepository)
                    nestRestApi(projectsRepository, nestedRepository)

                    get("/version") {
                        call.respondText("Some version")
                    }
                }
            }
        }.start(wait = true)
    }
}
