package pncluster

import akka.actor.{ActorSystem, AddressFromURIString, Props}
import com.typesafe.config.ConfigFactory
import akka.remote.routing.RemoteRouterConfig
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.{Router, ConsistentHashingPool, RoundRobinPool}

object startClient extends App {

	val config = 
		args.length match {
			case 1 => ConfigFactory.load.getConfig(args(0))
			case _ => ConfigFactory.load.getConfig("client")
		}

        val system = ActorSystem("ProperNamesClusterApp", config) 
	val numberMappers = ConfigFactory.load.getInt("number-mappers")
	val numberReducers = ConfigFactory.load.getInt("number-reducers")

	val master = system.actorOf(Props[MasterActor], name = "master")

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
		case Result(result) => master ! Result(result)
		case Done => master ! Done
	}

	Thread.sleep(5000)
	println("SENDING BOOKS...")

	for (book <- library.books) {
		master ! BookRouter(book, mappers)
	}
	
	master ! Flush(mappers)
}
