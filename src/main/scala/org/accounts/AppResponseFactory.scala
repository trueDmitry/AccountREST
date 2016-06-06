package org.accounts


import spray.json._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import spray.json._
import org.accounts.requests.User
import org.accounts.requests.SprayJsonProtocol._

//import org.accounts.AppResponseJsonProtocol._


//object AppResponseJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
//  implicit val StringResponseFormat = jsonFormat2(AppResponseFactory.StringResponse)
//  implicit val ListResponseFormat = jsonFormat2(AppResponseFactory.ListResponse)
//}

object AppResponseFactory extends DefaultJsonProtocol with SprayJsonSupport{
  def objectList(users: List[User]) = {
    apply (200,users)
  }

  def error() = {
    apply(400, "Error")
  }

  def error(e: Exception) = {
    apply(400, e.getMessage)
  }


  implicit val StringResponseFormat = jsonFormat2(AppResponseFactory.StringResponse)
  implicit val ListResponseFormat = jsonFormat2(AppResponseFactory.ListResponse)


  //  abstract class AppResponse(code: Int)s

  case class StringResponse(code: Int, message: String)
  case class ListResponse(code: Int, message: List[User])

  def apply(code: Int, userList: List[User]): String = {
    new ListResponse(code, userList).toJson.compactPrint
  }

  def apply(code: Int, message: String): String = {
    new StringResponse(code, message).toJson.compactPrint
  }

  def success(): String = {
    apply(200, "Success")
  }

  def success(msg : String): String = {
    apply(200, msg)
  }


  def wrongPassword: String = {
    apply(404, "Wrong password")
  }


  def invalidJSON  : String ={
    apply(400,"Invalid JSON")
  }

  def loginNotUnic() : String = {
    apply(404,"Someone already has that login")
  }

}


