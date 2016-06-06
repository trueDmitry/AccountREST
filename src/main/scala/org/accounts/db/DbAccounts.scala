package org.accounts.db

import org.accounts.requests._
import org.accounts.requests.User
import org.accounts.requests.SprayJsonProtocol._
import org.accounts.{AppResponseFactory, _}
import org.accounts.db.MongoDBConnection._
import org.accounts.Boot._

import reactivemongo.api.commands.{LastError, WriteResult}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONObjectID}
import reactivemongo.core.errors.DatabaseException
import spray.http.DateTime
import spray.httpx.marshalling.ToResponseMarshallable
import spray.json._

import scala.util.{Failure, Success}
import scala.annotation.elidable
import scala.annotation.elidable._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import ExecutionContext.Implicits.global

object DbAccounts {


  def setSessionTime(login: String, sTime: Int) = {
    val selector = BSONDocument("login" -> login)
    val doc = BSONDocument("session_time" -> sTime)
    dbUpdate(selector, doc)
  }

  def getSessionTime(login: String): String = {
    val dbUser = dbFindByLogin(login)
    val sTime = if (dbUser.sessionTime == Int.MaxValue) {
      setSessionTime(login, DEFAULT_SESSIONTIME)
      DEFAULT_SESSIONTIME
    } else {
      dbUser.sessionTime
    }
    AppResponseFactory.success(sTime.toString)
  }


  //todo extract method for deleteGroup ?
  // todo use pull
  def deleteRole(id: String, role: String): String = {
    val dbUser = dbFindById(id)

    val newList = dbUser.roles.filter(!_.equals(role))

    if (newList.length < dbUser.roles.length) {
      val selector = UserBSONWriter.idSelector(id)
      val doc = BSONDocument("roles" -> newList)
      dbUpdate(selector, doc)
    } else {
      AppResponseFactory.success
    }
  }


  //todo extract method for incertGroup ?
  // todo use mongo functions
  def insertRole(id: String, role: String): String = {
    val dbUser = dbFindById(id)

    var arrBuf = dbUser.roles.toBuffer

    if (!arrBuf.contains(role)) {
      arrBuf += role
      val selector = UserBSONWriter.idSelector(id)
      val doc = BSONDocument("roles" -> arrBuf.toArray)
      dbUpdate(selector, doc)
    } else {
      AppResponseFactory.success
    }
  }


  // todo use pull
  def deleteGroup(id: String, idGroup: String): String = {
    val dbUser = dbFindById(id)

    val newGroupList = dbUser.groups.filter(!_.equals(idGroup))

    if (newGroupList.length < dbUser.groups.length) {
      val selector = UserBSONWriter.idSelector(id)
      val doc = BSONDocument("groups" -> newGroupList)
      dbUpdate(selector, doc)
    } else {
      AppResponseFactory.success
    }
  }


  private def dbFindById(id: String) = {
    dbList(selector = UserBSONWriter.idSelector(id)).head
  }

  def insertGroup(id: String, idGroup: String): String = {
    val dbUser = dbFindById(id)

    var gArrBuf = dbUser.groups.toBuffer

    if (!gArrBuf.contains(idGroup)) {
      gArrBuf += idGroup
      val selector = UserBSONWriter.idSelector(id)
      val doc = BSONDocument("groups" -> gArrBuf.toArray)
      dbUpdate(selector, doc)
    } else {
      AppResponseFactory.success
    }
  }

  private def dbSetEnable(id: String, flag: Boolean): String = {
    val selector = UserBSONWriter.idSelector(id)
    val doc = BSONDocument("enabled" -> flag)
    dbUpdate(selector, doc)
  }

  def disableUser(id: String): String = {
    dbSetEnable(id, false)
  }

  def enableUser(id: String): String = {
    dbSetEnable(id, true)
  }


  private def dbFindByLogin(login: String) = {
    //todo whaif not found
    dbList(selector = BSONDocument("login" -> login)).head
  }

  def updateInfoByUser(uui: UserUpdatesInfo): String = {
    val dbUser = dbFindByLogin(uui.login)

    if (dbUser.hash == uui.hash) {
      val idSelector = UserBSONWriter.idSelector(dbUser.id)
      val doc: BSONDocument = UserBSONWriter.write(uui)

      dbUpdate(idSelector, doc)
    } else {
      AppResponseFactory.wrongPassword
    }
  }

  def updatePasswordByUser(uup: UserUpdatesPassword): String = {
    val dbUser: User = dbFindByLogin(uup.login)

    val idSelector = UserBSONWriter.idSelector(dbUser.id)
    if (dbUser.hash.equals(uup.oldPassword.hashCode.toString)) {
      val doc = BSONDocument("hash" -> uup.newPassword.hashCode.toString)
      dbUpdate(idSelector, doc)
    } else {
      AppResponseFactory.wrongPassword
    }

  }

  def updatePassword(user: User): String = {
    // todo need full user to update only one field
    val selector = UserBSONWriter.idSelector(user.id)
    val doc = UserBSONWriter.writeUpdatePass(user)
    dbUpdate(selector, doc)
  }

  def update(user: User): String = {
    val selector = UserBSONWriter.idSelector(user.id)
    val doc = UserBSONWriter.write(user)
    dbUpdate(selector, doc)
  }

  //ReactiveMongo also supports the MongoDB findAndModify pattern.

  private def dbUpdate(selector: BSONDocument, doc: BSONDocument): String = {
    val modifier = BSONDocument("$set" -> doc)
    val updateFuture: Future[WriteResult] = userColl.update(selector, modifier)
    val completeFuture = updateFuture.map {
      writeResult => {
        AppResponseFactory success
      }
    }

    try {
      Await.result(completeFuture, 5 seconds)
    } catch {
      case e: Exception => {
        AppResponseFactory error (e)
      }
    }
  }

  // mb one thread ?
  def create(user: User): String = {
    try {
      trowTestException(user)

      var doc: BSONDocument = UserBSONWriter.write(user)
      doc = doc.add("created" -> DateTime.now.clicks)
      val sessionTime = if (user.sessionTime == 0) {
        DEFAULT_SESSIONTIME
      } else {
        user.sessionTime
      }
      doc = doc.add("session_time" -> user.sessionTime)

      val insertFuture: Future[WriteResult] = userColl.insert(doc)

      val completeFuture = insertFuture.map {
        writeResult => AppResponseFactory.success
      }

      Await.result(completeFuture, 5 seconds)
    } catch {
      case e: DatabaseException => {
        if (dbFindByLogin(user.login) != null) {
          AppResponseFactory loginNotUnic
        } else {
          AppResponseFactory error()
        }
      }
      case e: Throwable => {
        AppResponseFactory error()
      }
    }
  }

  private def dbList(selector: BSONDocument): List[User] = {
    val docListFuture = userColl.find(selector).cursor().collect[List]()
    val completeFuture = docListFuture.map {
      docList => docList map (doc => UserBSONReader.read(doc))
    }
    Await.result(completeFuture, 5 seconds)
  }

  def list(ul: UserList): String = {
    var s = BSONDocument()

    if (!"".equals(ul.id)) {
      s = s.add("_id" -> BSONObjectID(ul.id))
    }
    if (!"".equals(ul.enabled)) {
      s = s.add("enabled" -> ul.enabled.toBoolean)
    }
    if (!"".equals(ul.login)) {
      s = s.add("login" -> ul.login)
    }
    if (!"".equals(ul.email)) {
      s = s.add("email" -> ul.email)
    }
    if (!"".equals(ul.name)) {
      s = s.add("name" -> ul.name)
    }
    if (!"".equals(ul.secondName)) {
      s = s.add("secondName" -> ul.secondName)
    }
    if (ul.roles.length > 0) {
      s = s.add("roles" -> ul.roles)
    }
    if (ul.groups.length > 0) {
      s = s.add("roles" -> ul.groups)
    }
    //    if( ul.permissions.length > 0 ){
    //      s = s.add("roles" -> ul.groups)
    //    }
    //    if( ul.info > 0 ){
    //      s = s.add("roles" -> ul.groups)
    //    }
    if (ul.created.length > 0) {
      val sort = ul.created.sorted
      s = s.add("created" -> BSONDocument(
        "$gte" -> sort(0),
        "$lte" -> sort.last
      ))
    }
    if (ul.sessionTime.length > 0) {
      val sort = ul.sessionTime.sorted
      s = s.add("session_time" -> BSONDocument(
        "$gte" -> sort(0),
        "$lte" -> sort.last
      ))
    }
    AppResponseFactory objectList (dbList(s))
  }

  val DEFAULT_SESSIONTIME = 15 * 60

  // initialisation
  {
    userColl.indexesManager.ensure(Index(Seq("login" -> IndexType.Text), background = true, unique = true))
  }

}
