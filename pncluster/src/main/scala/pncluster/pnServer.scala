package pncluster

import akka.actor.{ActorSystem, Props, Actor}
import com.typesafe.config.ConfigFactory
import akka.cluster._
import akka.cluster.ClusterEvent._

object startServer extends App {
// Starts an actor system with the "server" configuration
// sbt ./runMain pncluster.startServer

	val config = 
		args.length match { 
			case 1 => ConfigFactory.load.getConfig(args(0))
			case _ => ConfigFactory.load.getConfig("server")
		}

        val system = ActorSystem("ProperNamesClusterApp", config) 

	//Create an actor to handle cluster messages
	system.actorOf(Props[ClusterActor])
}

class ClusterActor extends Actor {
/*
 * [Citation]
 * https://medium.com/@diego_pacheco/running-multi-nodes-akka-cluster-on-docker-24699a246c28
*/
	override def preStart = Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])

	def receive = {
		case MemberUp(member) => println("member up")
		case event => println(s"event ${event.toString}")

	}
}
