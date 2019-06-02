package pncluster

import akka.actor.ActorRef

case class OnlineBook(title:String, url:String)
case class BookRouter(onlineBook:OnlineBook, router:ActorRef)
case class Name(name:String, title:String) 
case class Flush(router:ActorRef)
case class Result(result:String)
case object Done
case object Dummy

object library {
	val books = Seq(
        OnlineBook("pg580", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg580.txt"),
        OnlineBook("pg968", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg968.txt"),
        OnlineBook("pg807", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg807.txt"),
        OnlineBook("pg1400", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1400.txt"),
        OnlineBook("pg98", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg98.txt"),
        OnlineBook("pg20795", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg20795.txt"),
        OnlineBook("pg1023", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg1023.txt"),
        OnlineBook("pg883", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg883.txt"),
        OnlineBook("pg821", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg821.txt"),
        OnlineBook("pg730", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg730.txt"),
        OnlineBook("pg700", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg700.txt"),
        OnlineBook("pg766", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg766.txt"),
        OnlineBook("pg967", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg967.txt"),
        OnlineBook("pg699", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg699.txt"),
        OnlineBook("pg19337", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg19337.txt"),
        OnlineBook("pg963", 
	"http://reed.cs.depaul.edu/lperkovic/csc536/homeworks/gutenberg/pg963.txt"))
}
