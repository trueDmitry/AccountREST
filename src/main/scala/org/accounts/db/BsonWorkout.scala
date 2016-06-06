package org.accounts.db

import org.accounts.requests.{User, UserUpdatesInfo}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, BSONValue}

//https://gist.github.com/rleibman/7103325d0193be268ed7

object UserBSONWriter extends BSONDocumentWriter[User] {
  //login: String, hash: String, email : String, name : String, secondName : String, info : List[String]
  def write(uui: UserUpdatesInfo): BSONDocument = {
    BSONDocument(
      "emial" -> uui.email,
      "name" -> uui.name,
      "secondName" -> uui.secondName,
      "info" -> uui.info
    )
  }

  def write(user: User): BSONDocument = BSONDocument(
    "name" -> user.name,
    "login" -> user.login,
    "hash" -> user.hash,
    "info" -> user.info,
    "groups" -> user.groups,
    "roles" -> user.roles,
    "enabled" -> user.enabled
  )

  def writeUpdatePass(user: User): BSONDocument = BSONDocument(
    "hash" -> user.hash
  )

  def idSelector(id: String) = BSONDocument("_id" -> BSONObjectID(id))

}

object UserBSONReader extends BSONDocumentReader[User] {

  class SimpleBson(val bson: BSONDocument) {

    def getBoolean(valName: String) : Boolean ={
      bson.getAs[Boolean](valName).get
      //if (tryres.isSuccess) tryres.get else throw // :S
    }
    def getLong(valName: String) : Long ={
      val tryres = bson.getAsTry[Long](valName)
      if (tryres.isSuccess) tryres.get else Long.MaxValue // :S
    }

    def getInt(valName: String): Int = {
      //bson.getAs[String](valName).get
      val tryres = bson.getAsTry[Int](valName)
      if (tryres.isSuccess) tryres.get else Int.MaxValue // :S
    }

    def getString(valName: String): String = {
      val tryres = bson.getAsTry[String](valName)
      if (tryres.isSuccess) tryres.get else ""
    }

    def getObjectId(valName: String): String = {
      val tryres = bson.getAsTry[BSONObjectID](valName)
      if (tryres.isSuccess) tryres.get.stringify else ""
    }

    def getArray(valName : String): List[String] = {
      val tryRes = bson.getAsTry[List[String]](valName)
      if (tryRes.isSuccess) tryRes.get else List[String]()
    }
  }

  implicit def bson2simpleBson(bson: BSONDocument) = new SimpleBson(bson)

  def read(bson: BSONDocument): User = User(
    id = bson getObjectId "_id",
    name = bson getString "name",
    login = bson getString "login",
    hash = bson getString "hash",
    info = bson getArray "info",
    groups = bson getArray "groups",
    roles= bson getArray "roles",
    sessionTime = bson getInt "session_time",
    created = bson getLong "created",
    enabled = bson getBoolean "enabled",
    email = bson getString "email"
  )
}