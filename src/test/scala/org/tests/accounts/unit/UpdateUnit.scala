package org.tests.accounts.unit

import akka.actor.ActorSystem
import org.accounts.db.MongoDBConnection._
import org.accounts.requests.{User, UserList}
import org.accounts.requests.SprayJsonProtocol._


import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.concurrent.ScalaFutures
import reactivemongo.bson.BSONDocument
import spray.client.pipelining._

import scala.concurrent.Await

class UpdateUnit extends FunSuite with ScalaFutures  with BeforeAndAfter with mSuit {

  val login = "trueDmitry"

  var userUpdate : User = null
  var userListByID : UserList = null

  val userListByLogin = UserList(id = "", enabled = "", login = login, email="", name="", secondName="",
    roles = List(), groups= List(), permissions= List(), info= List(), created= List(), sessionTime= List())



  override def getPath(): String = "/update"

  def getID(str: String) : String = {
    val pattern = "([^\\\"]{24})".r
    (pattern findFirstIn  str).get
  }

  before {
    cleanUp()

    // add one user
    send(Post(serverUrl + "/create", userOne))
    val res = send(Post(serverUrl + "/list", userListByLogin))
    val id = getID(res)

    userUpdate = User(id, name = "two", login = login, email = "test@mdfgaail.com", hash = "1adasd23456789104564646", info = List[String](),
      groups = List[String](), roles = List[String](), sessionTime = 15 * 60, created = 0, enabled = true)

    userListByID = UserList(id = id, enabled = "", login = "", email="", name="", secondName="",
      roles = List(), groups= List(), permissions= List(), info= List(), created= List(), sessionTime= List())
  }

  test ("update user one"){
    val res = send(Post(url,userUpdate))
    println(res)
    assert(resSuccess.equals(res))
    val storedUser = send(Post(serverUrl + "/list", userListByID))
    println(storedUser)
    // some assertion storedUser and userUpdate
  }






}