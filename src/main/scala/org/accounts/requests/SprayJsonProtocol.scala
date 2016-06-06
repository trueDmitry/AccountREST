package org.accounts.requests

import spray.json._
import spray.http.{ContentTypes, HttpEntity}
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.Marshaller

//object SprayJsonProtocol extends  UPUProtocol with  UserJsonProtocol{
object SprayJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  /**
    * UserList
    *
    */
  implicit val UListFormat = jsonFormat12(UserList)
  implicit val UListMarshaller = Marshaller.of[UserList](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }


  /**
    * UserLogin
    *
    */
  implicit val ULFormat = jsonFormat1(UserLogin)
  implicit val ULMarshaller = Marshaller.of[UserLogin](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }

  /**
    * UserRole
    *
    */
  implicit val URFormat = jsonFormat2(UserRole)
  implicit val URMarshaller = Marshaller.of[UserRole](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }

  /**
    * UserGroup
    *
    */
  implicit val UGFormat = jsonFormat2(UserGroup)
  implicit val UGMarshaller = Marshaller.of[UserGroup](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }


  /**
    * UserId
    *
    */
  implicit val UIdFormat = jsonFormat1(UserId)
  implicit val UIdMarshaller = Marshaller.of[UserId](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }

  /**
    * UserUpdateInfo
    *
    */
  implicit val UUIFormat = jsonFormat6(UserUpdatesInfo)
  implicit val UUIMarshaller = Marshaller.of[UserUpdatesInfo](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }


  /**
    * UserUpdatePassword
    */
  implicit val UPUFormat = jsonFormat3(UserUpdatesPassword)
  implicit val UPUMarshaller = Marshaller.of[UserUpdatesPassword](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }

  /**
    * User
    */
  implicit val userFormat = jsonFormat11(User)
  implicit val userMarshaller = Marshaller.of[User](ContentTypes.`application/json`) {
    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, value.toJson.compactPrint))
  }

  // dont works and dont needed bcs user ia a case class
  //  implicit val userUnMarshaller = Unmarshaller[User](ContentTypeRange*) {
  //    case HttpEntity.NonEmpty (ContentTypes.`application/json`, data) =>
  //      JsonParser(data.toString).convertTo[User]
  //  }


}
