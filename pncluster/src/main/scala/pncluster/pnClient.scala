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

        val system = ActorSystem("ProperNamesClusterApp", config) 
	val numberMappers = ConfigFactory.load.getInt("number-mappers")
	val numberReducers = ConfigFactory.load.getInt("number-reducers")

	val client = system.actorOf(Props[ClientActor], name = "client")

	//TODO: move seeds to application.conf
	val addresses = Seq(
		AddressFromURIString("akka.tcp://ProperNamesApp@127.0.0.1:2552"),
		AddressFromURIString("akka.tcp://ProperNamesApp@127.0.0.1:2553"))

	//TODO: set up routers in cluster
	val reducers = system.actorOf(
		RemoteRouterConfig(
			ConsistentHashingPool(
				numberReducers, 
				hashMapping = hashMapping), 
			addresses).props(Props[Reducer]))

	//TODO: set up routers in cluster
	val mappers = system.actorOf(
		RemoteRouterConfig(
			RoundRobinPool(numberMappers), addresses).
			props(Props(classOf[Mapper], reducers)))

	def hashMapping: ConsistentHashMapping = {
		case Name(name, title) => name
		case Result(result) => client ! Result(result)
		case Done => client ! Done
	}

	Thread.sleep(5000)
	println("SENDING BOOKS...")

//	for (book <- library.books) {
//		client ! BookRouter(book, mappers)
//	}
	library.books.foreach(tell(BookRouter(_,mappers), client))
	
	client ! Flush(mappers)
}

class ClientActor extends Actor {

        var busyReducersPending = ConfigFactory.load.getInt("number-reducers")

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
