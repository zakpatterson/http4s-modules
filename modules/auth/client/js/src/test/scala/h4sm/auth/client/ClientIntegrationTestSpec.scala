package h4sm.auth.client

import arbitraries._
import cats.data.StateT
import cats.implicits._
import h4sm.auth.client.implicits._
import h4sm.auth.comm.UserRequest
import scala.concurrent.Future
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalacheck.Arbitrary

class ClientIntegrationTestSpec extends AsyncFlatSpec with ScalaCheckPropertyChecks with Matchers {
  implicit override def executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val authClient = new Client[Future]{
    override def base: String = "http://localhost:8080/users"
  }

  "the auth client" should "be in test" in {
    authClient.isTest.map(x => assert(!x))
  }

  it should "allow login after signup" in {   
    val user = implicitly[Arbitrary[UserRequest]].arbitrary.sample.get

    val state = for {
      _ <- StateT.liftF(authClient.signup(user))
      _ <- authClient.getSession(user)
      p <- authClient.currentUser
      _ <- authClient.logout
      p2 <- authClient.currentUser.map(_.some).recover{ case _ => none }
      _ <- StateT.liftF(authClient.delete(user.username))
    } yield {
      println("Got session")
      println(p)
      p.username should equal (user.username)
      p2 should equal (none)
    }

    state.runEmptyA
  }
}
