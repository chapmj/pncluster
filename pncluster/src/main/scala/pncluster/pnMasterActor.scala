package pncluster

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.{Address, AddressFromURIString}
import akka.routing.ConsistentHashingRouter.{ConsistentHashMapping}
import akka.routing.{Broadcast, RoundRobinPool}
import akka.routing.{Router, ConsistentHashingPool}
import com.typesafe.config.ConfigFactory

class MasterActor extends Actor {

        val numberReducers = ConfigFactory.load.getInt("number-reducers")

        // Keep track of how many reducers are busy
        var pending = numberReducers

        def receive = 
        {
                case BookRouter(OnlineBook(name, url), router) => 
		println("MASTER SENDING BOOK TO MAPPERS")
		router ! OnlineBook(name, url)

                case Flush(router) =>
		router ! Broadcast(Flush)

		case Result(result) =>
		println(result)

                case Done => 
		pending -= 1
		if (pending == 0) { context.system.terminate }
        }

}
