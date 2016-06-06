package org.accounts.db

import org.accounts.Config.MongoDB._
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

object MongoDBConnection {
  val driver = new MongoDriver
  val connection = driver.connection(List(mongoHost))
  val db = connection(dbName)
  val userColl: BSONCollection = db(userCollection)

  def init(): Unit = {}
}
