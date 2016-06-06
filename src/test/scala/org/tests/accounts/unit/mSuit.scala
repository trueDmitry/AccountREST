package org.tests.accounts.unit

import akka.actor.ActorSystem
import org.accounts.db.MongoDBConnection._
import org.accounts.requests.User
import reactivemongo.bson.BSONDocument
import spray.client.pipelining._
import spray.http.HttpRequest

import scala.concurrent.Await
import scala.concurrent.duration._

trait mSuit {
  val serverUrl = "http://localhost:8081"
  var url: String = "http://localhost:8081" + getPath() // need enum

  val userOne = User("", name = "Dima", login = "trueDmitry", email = "test@mail.com", hash = "123456789104564646", info = List[String](),
    groups = List[String](), roles = List[String](), sessionTime = 15 * 60, created = 0, enabled = true)

  val userError = User("", name = "throw_me", login = "throw_me", email = "throw_me", hash = "throw_me", info = List[String](),
    groups = List[String](), roles = List[String](), sessionTime = 15 * 60, created = 0, enabled = true)


  val resSuccess = "{\"code\":200,\"message\":\"Success\"}"
  val resMailformed = "{\"code\":400,\"message\":\"Invalid JSON\"}"
  val resNonUnicLogin = "{\"code\":404,\"message\":\"Someone already has that login\"}"
  val resError = "{\"code\":400,\"message\":\"Error\"}"

  def cleanUp() = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures

    //println("killng collection")
    Await.ready(userColl.remove(BSONDocument()), 5 seconds)
  }

  def getPath(): String

  def creatUserCmd: String = {
    "curl -X POST " +
      url +
      " -H \"Content-Type: application/json\" -d \"{\"\"id\"\":\"\"\"\", \"\"name\"\":\"\"Ivan\"\",\"\"login\"\":\"\"loginIvan\"\", \"\"hash\"\": \"\"46392755\"\", \"\"info\"\" : [], \"\"groups\"\" : [], \"\"roles\"\" : [], \"\"sessionTime\"\" : 0, \"\"created\"\" : 0 , \"\"enabled\"\": true, \"\"email\"\" : \"\"mail@domen.com\"\"}\""
  }

  def creatUserCmdMalformed() : String = {
    "curl -X POST " +
      url +
      " -H \"Content-Type: application/json\" -d \"{\"\"id\"\" \"\"\"\", \"\"name\"\":\"\"Ivan\"\",\"\"login\"\":\"\"loginIvan\"\", \"\"hash\"\": \"\"46392755\"\", \"\"info\"\" : [], \"\"groups\"\" : [], \"\"roles\"\" : [], \"\"sessionTime\"\" : 0, \"\"created\"\" : 0 , \"\"enabled\"\": true, \"\"email\"\" : \"\"mail@domen.com\"\"}\""
  }


  def send(httpReq: HttpRequest): String = {
    implicit val system = ActorSystem()
    import system.dispatcher
    // execution context for futures

    val pipeline = sendReceive // fe
    val responseRedyFuture = pipeline(httpReq).map(
        postRes => {
          postRes.entity.asString
        }
      )
    Await.result(responseRedyFuture, 5 seconds)
  }

}
