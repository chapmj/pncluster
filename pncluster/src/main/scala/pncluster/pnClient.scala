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

	val addresses = Seq(
		AddressFromURIString("akka.tcp://ProperNamesApp@127.0.0.1:2552"),
		AddressFromURIString("akka.tcp://ProperNamesApp@127.0.0.1:2553"))

	val reducers = system.actorOf(
		RemoteRouterConfig(
			ConsistentHashingPool(
				numberReducers, 
				hashMapping = hashMapping), 
			addresses).props(Props[Reducer]))

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

	for (book <- library.books) {
		client ! BookRouter(book, mappers)
	}
	
	client ! Flush(mappers)
}

class ClientActor extends Actor {

        val numberReducers = ConfigFactory.load.getInt("number-reducers")

        // Keep track of how many reducers are busy
        var pending = numberReducers

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
                pending -= 1
                if (pending == 0) { context.system.terminate }
        }
}
