package com.example

import sttp.tapir._
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.Task
import zio.ZIO
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

object Endpoints {
  val baseEndpoint =
    endpoint
      .errorOut(statusCode and stringBody)

  val helloEndpoint = baseEndpoint.get
    .in("hello" / path[String]("name"))
    .out(stringBody)

  val helloServerEndpoint: ZServerEndpoint[Any, Any] =
    helloEndpoint.serverLogicSuccess(name => ZIO.succeed(s"Hello ${name}"))

  val crashEndpoint = baseEndpoint.get
    .in("crash")
    .out(stringBody)

  val crashServerEndpoint: ZServerEndpoint[Any, Any] =
    crashEndpoint.serverLogicSuccess(_ => throw new Error("Crash!"))

  val apiEndpoints: List[ZServerEndpoint[Any, Any]] =
    List(helloServerEndpoint, crashServerEndpoint)

  val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "example", "1.0.0")

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints
}
