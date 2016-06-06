package org.accounts.routes

import org.accounts.AppResponseFactory
import spray.routing.{HttpService, MalformedRequestContentRejection, RejectionHandler}
import org.accounts.requests._
import org.accounts.requests.SprayJsonProtocol._
import org.accounts.db.DbAccounts

trait AccountRoute extends HttpService {

  implicit def myRejectionHandler = RejectionHandler {
    case MalformedRequestContentRejection(errorMsg, _) :: _ =>
      //complete(AppResponseFactory.invalidJSON)
      complete(errorMsg)
  }

  val routs =
    post {

      path("create") {
        entity(as[User]) {
          user => complete(DbAccounts.create(user))
        }
      } ~
        path("update") {
          entity(as[User]) {
            user => complete(DbAccounts.update(user))
          }
        } ~
        path("update_password") {
          entity(as[User]) {
            user => complete(DbAccounts.updatePassword(user))
          }
        } ~
        path("profile" / "update_password") {
          entity(as[UserUpdatesPassword]) {
            uup => complete(DbAccounts.updatePasswordByUser(uup))
          }
        } ~
        path("profile" / "update_info") {
          entity(as[UserUpdatesInfo]) {
            uui => complete(DbAccounts.updateInfoByUser(uui))
          }
        } ~
        path("list") {
          entity(as[UserList]) {
            ul => complete(DbAccounts.list(ul))
          }
        } ~
        path("enable") {
          entity(as[UserId]) {
            uid => complete(DbAccounts.enableUser(uid.id))
          }
        } ~
        path("disable") {
          entity(as[UserId]) {
            uid => complete(DbAccounts.disableUser(uid.id))
          }
        } ~
        path("insert_group") {
          entity(as[UserGroup]) {
            ug => complete(DbAccounts.insertGroup(ug.id, ug.idGroup))
          }
        } ~
        path("delete_group") {
          entity(as[UserGroup]) {
            ug => complete(DbAccounts.deleteGroup(ug.id, ug.idGroup))
          }
        } ~
        path("insert_role") {
          entity(as[UserRole]) {
            ur => complete(DbAccounts.insertRole(ur.id, ur.role))
          }
        } ~
        path("delete_role") {
          entity(as[UserRole]) {
            ur => complete(DbAccounts.deleteRole(ur.id, ur.role))
          }
        } ~
        path("session_time") {
          entity(as[UserLogin]) {
            ul => complete(DbAccounts.getSessionTime(ul.login))
          }
        } ~
        path("permissions") {
          complete(???)
        }


    }

}
