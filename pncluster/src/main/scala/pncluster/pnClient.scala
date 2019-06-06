package pncluster

import akka.actor.{Actor, ActorRef, Props, ActorSystem, Address, AddressFromURIString}
import akka.remote.routing.RemoteRouterConfig
import akka.routing.{Broadcast, Router, ConsistentHashingPool, RoundRobinPool}
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import com.typesafe.config.ConfigFactory

object startClient extends App {
	val config = 
		args.length match {
			case 1 => ConfigFactory.load.getConfig(args(0))
			case _ => ConfigFactory.load.getConfig("client")
		}

	val system = ActorSystem(ConfigFactory.load.getString("system-name"), config) 

	val client = system.actorOf(Props[ClientActor], name = "client")

	//TODO: move seeds to application.conf
	val addresses = Seq(
		AddressFromURIString(s"akka.tcp://${systemName}@127.0.0.1:2552"),
		AddressFromURIString(s"akka.tcp://${systemName}@127.0.0.1:2553"))

	def hashMapping: ConsistentHashMapping = {
		case Name(name, title) => name
		case Result(result) => client ! Result(result)
		case Done => client ! Done
	}

	Thread.sleep(5000)
	println("SENDING BOOKS...")
	library.books.foreach(tell(BookRouter(_,mappers), client))
	
	client ! Flush(mappers)
}

class ClientActor extends Actor {

        def receive =
        {
                case BookRouter(OnlineBook(name, url), router) =>
                println("SENDING BOOK TO MAPPERS")
                router ! OnlineBook(name, url)

                case Flush(router) =>
                router ! Broadcast(Flush)

                case Result(result) =>
                println(result)

                case Done =>
                busyReducersPending -= 1
                if (pending == 0) { context.system.terminate }
        }
}
