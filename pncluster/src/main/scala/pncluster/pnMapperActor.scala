package pncluster

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.Broadcast
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.HashMap
import scala.io.Source 
import scala.util.matching.Regex

class Mapper(reducers: ActorRef) extends Actor {

	println("MAPPER STARTED")
        def receive = 
	{
		case OnlineBook(name, url) => 
                process(OnlineBook(name, url))
		println("MAPPER COMPLETED PROCESSING BOOK")

          	case Flush =>
		reducers ! Broadcast(Flush)

		case _ => println("PATTERN ERROR")

        }

	def getProperNames(words:String) : List[String] = { 
	// Return list of names that match the "proper name" pattern
		var names:List[String] = List()
		val namePattern = "[A-Z]([a-z]|(['][A-Z]))[a-z]*".r
		val nameOpts = 
			for (word <- words.split(" ")) 
			yield namePattern.findFirstMatchIn(word)

		for (name <- nameOpts) {
			name match {
				case Some(_) => names = name.get.toString :: names
				case None => None
			}
		}
		names
	}

        def process(onlineBook:OnlineBook) = {
                var nameHashes = HashMap[String, Any]()
                val contents = Source.fromURL(onlineBook.url).mkString
                val properNames = getProperNames(contents)

                properNames.foreach(sendNameConsistent)

                def sendNameConsistent(name:String) = {
		// Forward unique names to reducer actors and add names to
		// already found names hashmap.  A dummy case clas is used as
		// filler since we don't care what value a hashkey has.
			if (!(nameHashes contains name)) {
				reducers ! Name(name,onlineBook.title)
				nameHashes += (name -> Dummy)
			}
                }
        }
}
