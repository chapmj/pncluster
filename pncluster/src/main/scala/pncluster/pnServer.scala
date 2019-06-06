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

        val system = ActorSystem(ConfigFactory.load.getString("system-name"), config) 

	//Create an actor to handle cluster messages
	system.actorOf(Props[ClusterActor])
}

class ClusterActor extends Actor {
/*
 * [Citation]
 * https://medium.com/@diego_pacheco/running-multi-nodes-akka-cluster-on-docker-24699a246c28
 * https://doc.akka.io/docs/akka/current/cluster-usage.html
*/
	val cluster = Cluster(context.system)
	override def preStart() = cluster.subscribe(self, classOf[MemberUp])
	override def postStop() = cluster.unsubscribe(self)

	def receive = {
	// The events to track the life-cycle of members are:

	/* MemberJoined - A new member has joined the cluster and
	its status has been changed to Joining*/

	/* MemberUp - A new member has joined the cluster and its
	status has been changed to Up*/
		case MemberUp(member) => 
		println("member up")
		register(member)

	/* MemberExited - A member is leaving the cluster and its
	status has been changed to Exiting Note that the node might already have
	been shutdown when this event is published on another node.*/

	/* MemberRemoved - Member completely removed from the
	cluster.*/

	/* UnreachableMember - A member is considered as
	unreachable, detected by the failure detector of at least one other
	node.*/

	/* ReachableMember - A member is considered as reachable
	again, after having been unreachable. All nodes that previously detected
	it as unreachable has detected it as reachable again.*/

		case CurrentClusterState =>
		state.members
			.filter(_.status == MemberStatus.Up)
			.foreach(register)
			//TODO: register is a func that notifies the client it
			//is available to accept jobs. Send some kind of
			//acknowledgement case class

		case event => 
		println(s"event ${event.toString}")
	}

}
