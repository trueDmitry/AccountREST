package org.tests.accounts.unit

import akka.actor.ActorSystem
import org.accounts.requests.User
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import spray.client.pipelining._
import scala.concurrent.duration._

import scala.concurrent.Await

class ListUnit extends FunSuite with ScalaFutures {

  var baseUrl : String = "http://localhost:8081"

  var url: String = baseUrl + "/list"
//  var user = User("Dima")


  test("test list all"){

    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures

    val pipeline = sendReceive
    val responseFuture = pipeline(Post(url)) map {
      postRes => postRes.entity.asString
    }

    val result  = Await result(responseFuture, 5 seconds)

    println(result)

  }



}