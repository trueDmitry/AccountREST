package org.tests.accounts.unit


import org.accounts.requests._
import org.accounts.db.MongoDBConnection._
import org.accounts.requests.SprayJsonProtocol._

import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import spray.http.{HttpEntity, _}
import spray.client.pipelining._
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.concurrent.ScalaFutures
import reactivemongo.bson.BSONDocument

//import ExecutionContext.Implicits.global


class CreateUnit extends FunSuite with ScalaFutures with BeforeAndAfter with mSuit{
  override def getPath(): String = "/create"

  before {
    cleanUp()
//    implicit val system = ActorSystem()
//    import system.dispatcher // execution context for futures
//
//    println("killng collection")
//    Await.ready(userColl.remove(BSONDocument()), 5 seconds)
  }


   test("testing create user") {
     println(url)
     val res = send(Post(url, userOne))
     assert(resSuccess.equals(res))
   }

  test("testing bad json") {
    import sys.process._
    var res = creatUserCmdMalformed.!!  // trailing \n
    assert(res != null)
    assert(res.contains(resMailformed))
  }


  test("some one already has that login"){
    var res = send(Post(url, userOne))
    res = send(Post(url, userOne))
    assertResult(resNonUnicLogin)(res)
  }


  test("internal error"){
    val res = send(Post(url, userError))
    assertResult(resError)(res)
  }

}