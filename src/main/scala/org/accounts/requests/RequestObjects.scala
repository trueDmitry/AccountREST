package org.accounts.requests


case class UserGroup(id: String, idGroup : String)

case class UserRole(id: String, role : String)

case class UserId(id: String)

case class UserLogin(login: String)

case class UserUpdatesPassword(login: String, oldPassword: String, newPassword: String)

case class UserUpdatesInfo(login: String, hash: String, email : String, name : String, secondName : String, info : List[String])

case class UserList (id: String, enabled: String, login : String, email: String, name : String, secondName : String,
                     roles : List[String], groups: List[String], permissions : List[String], info : List[String],
                     created : List[Long], sessionTime: List[Int] )


case class User(
                 id: String,
                 name: String,
                 login: String,
                 email : String,
                 hash: String,
                 info: List[String],
                 groups : List[String],
                 roles: List[String],
                 sessionTime : Int,
                 created : Long,
                 enabled : Boolean
               )
