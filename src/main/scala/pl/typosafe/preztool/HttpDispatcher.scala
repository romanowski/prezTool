package pl.typosafe.preztool

import akka.actor.Actor
import spray.http.{HttpResponse, HttpMethods, HttpRequest}

class HttpDispatcher extends Actor {
  override def receive: Receive = {
    case a => println(a)
  }
}
