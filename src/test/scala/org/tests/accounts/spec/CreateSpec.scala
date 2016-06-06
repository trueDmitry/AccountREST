package org.tests.accounts.spec

import org.accounts.routes.AccountRoute
import org.scalatest.FlatSpec
import org.scalatest.concurrent.ScalaFutures
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest

class CreateSpec extends FlatSpec with ScalatestRouteTest with HttpService with AccountRoute with ScalaFutures {
  val url = "/create"
  def actorRefFactory = system

}