package pncluster

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object startServer extends App {
// sbt ./runMain ProperNameFinderApp3.startServer

	val config = 
		args.length match { 
			case 1 => ConfigFactory.load.getConfig(args(0))
			case _ => ConfigFactory.load.getConfig("server1")
		}

        val system = ActorSystem("ProperNamesClusterApp", config) 
}
