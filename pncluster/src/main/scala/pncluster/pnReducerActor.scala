package pncluster

import akka.actor.{Actor, Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.HashMap


class Reducer extends Actor {
	println("REDUCER STARTED")
        var remainingMappers = ConfigFactory.load.getInt("number-mappers")
        var reduceMap = HashMap.empty[String, List[String]] 

        def receive = 
        {
		case Name(name, title) =>

                reduceMap.get(name) match {
			case Some(_) => 
				reduceMap += (name -> (title :: reduceMap(name)))
			case None => 
				reduceMap += (name -> List(title))
                }
                        
                case Flush => 
                        remainingMappers -= 1
                        if (remainingMappers == 0) {
                                printNamesBooks()
				context.parent ! Done 
                        }
        }

        def printNamesBooks() 
	{
		println("REDUCER COMPLETED WORK")
		for (personName <- reduceMap.keys) {
                  
			var line = "Name: " + personName + " Books: "
                  
                        for (bookName <- reduceMap(personName)) {
                                line += s"${bookName}, "
                        }

                        if (line.endsWith(", ")) {
                                line = line.substring(0, line.length - 2)
                        }

                        context.parent ! Result(line)
                }
        }
}
