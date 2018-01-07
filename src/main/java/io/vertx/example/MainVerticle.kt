package io.vertx.example

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.asyncsql.AsyncSQLClient
import io.vertx.ext.asyncsql.PostgreSQLClient


@Suppress("unused")
class MainVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    lateinit var postgreSQLClient: AsyncSQLClient

    override fun start(startFuture: Future<Void>) {

        print("EEEEE")
        log.info("blahhhh")
        setUpDatabase()
        val router = createRouter()

        vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config().getInteger("http.port", 8050)) { result ->
                    if (result.succeeded()) {
                        startFuture.complete()
                    } else {
                        startFuture.fail(result.cause())
                    }
                }
    }

    private fun createRouter() = Router.router(vertx).apply {
        get("/").handler(handlerRoot)
        get("/islands").handler(handlerIslands)
        get("/countries").handler(handlerCountries)
    }

    //
    // Handlers

    val handlerRoot = Handler<RoutingContext> { req ->
        println("reqqqqqq")
        val bo=testDatabase()
        req.response().end("Welcome!"+bo)
    }

    val handlerIslands = Handler<RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS)
    }

    val handlerCountries = Handler<RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS.map { it.country }.distinct().sortedBy { it.code })
    }

    //
    // Mock data

    private val MOCK_ISLANDS by lazy {
        listOf(
                Island("Kotlin", Country("Russiaa", "RUs")),
                Island("Stewart Island", Country("New Zealand", "NZ")),
                Island("Cockatoo Island", Country("Australia", "AU")),
                Island("Tasmania", Country("Australia", "AU"))
        )
    }

    //
    // Utilities

    /**
     * Extension to the HTTP response to output JSON objects.
     */
    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

    private fun setUpDatabase(){
      // To create a PostgreSQL client
      val postgreSQLClientConfig = JsonObject().put("database", "aba")
      postgreSQLClientConfig.put("username","abakus")
      postgreSQLClientConfig.put("password","abakus")
      postgreSQLClientConfig.put("maxPoolSize",10)
      postgreSQLClient = PostgreSQLClient.createNonShared(vertx, postgreSQLClientConfig)
    }

  private fun testDatabase():Boolean{
    var result: Boolean = true
    val blah = postgreSQLClient.getConnection({ res ->
        print("hiiiiii")
      if (res.succeeded()) {

        val connection = res.result()
          print("BBBBBBBBBB")
    log.info("kjkjkjkjkjkj")
          // Got a connection
        result = false
      } else {
          // Failed to get connection - deal with it
          print("AAAAAAAAAZZz")
        result = false
      }
    })


      postgreSQLClient.close()
      return result

  }
}

