package org.accounts

import scala.concurrent.duration._
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.accounts.db.MongoDBConnection
import spray.can.Http
import org.accounts.routes.MainActor

import scala.annotation.elidable
import scala.annotation.elidable._

//import Config._

object Boot extends App {

  implicit val system = ActorSystem(Config.actorSystemName)

  val service = system.actorOf(Props[MainActor], Config.serviceName)

  implicit val timeout = Timeout(Config.timeoutService.seconds)

  IO(Http) ? Http.Bind(service, interface = Config.interface, port = Config.port)

  MongoDBConnection.init()

  val debug = true
  //todo elidable doesnt works
  //@elidable(FINE)
  def trowTestException(any : AnyRef) = {
    if (any.toString.contains("throw_me")){
    //if (Config.debug && any.toString.contains("throw_me")){
      throw new Exception("throwed on request")
    }
  }

}