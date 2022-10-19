package com.example

import sttp.tapir.server.interceptor.log.DefaultServerLog
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zhttp.http.HttpApp
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio.{Console, ExitCode, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  val port = sys.env.get("http.port").map(_.toInt).getOrElse(8080)

  val app: HttpApp[Any, Throwable] =
    ZioHttpInterpreter().toHttp(Endpoints.all)

  val program: ZIO[Any, Throwable, ExitCode] =
    for {
      _ <- Console.printLine(
        s"Go to http://localhost:${port}/docs to open SwaggerUI."
      )
      _ <- Server.start(port, app)
    } yield ExitCode.success

  override def run: ZIO[Any, Any, Any] =
    program.debug
}
