import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import scala.collection.immutable
import scala.util.Random

object GraphFlow extends App {
  implicit val actorSystem = ActorSystem()
  implicit val flowMaterializer = ActorMaterializer()

  val in = Source(1 to 10)

  val out = Sink.foreach[Int](println)

  val f1, f3 = Flow[Int].map(_ + 10)

  val f2 = Flow[Int].map(_ * 5)

  val f4 = Flow[Int].map(_ + 0)

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val broadCast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    in ~> f1 ~> out
    //in ~> f1 ~> broadCast ~> f2 ~> merge ~> f3 ~> out
    //            broadCast ~> f4 ~> merge
    //in ~> broadCast ~> f2 ~> merge ~> out
    //      broadCast ~> f3 ~> merge
  })

  g.run()

  Thread.sleep(1000)

  actorSystem.shutdown()
  actorSystem.awaitTermination()
}