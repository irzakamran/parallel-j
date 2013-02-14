package j.lang.datatypes.array

import scala.collection.mutable.Stack

import j.lang.datatypes.array.types.JNumberTypes._
import j.lang.datatypes.array.JArrayFlag._
import j.lang.datatypes.JFuncRank

object JArrayFrame {
  def apply[T <% JArrayType : Manifest](frameLevels: List[JNumber], jar: JArray[T]) = {
    new JArrayFrame(frameLevels.map(jnum => jnum match {
      case i: JInt => i.v
      case inf: JInfinite => jar.rank
      case _ => throw new Exception() //TODO domain error
    }), jar)
  }
  
  def apply[T <% JArrayType : Manifest](rank: JNumber, jar: JArray[T]): JArrayFrame[T] = JArrayFrame(List(rank), jar)
  
  def createFrames[T <% JArrayType : Manifest](rs: List[JFuncRank], jar: JArray[T]) = JArrayFrame(rs.map(_ r1), jar)
  def createFrames[T <% JArrayType : Manifest, U <% JArrayType : Manifest](rs: List[JFuncRank], jar1: JArray[T], jar2: JArray[U]): (JArrayFrame[T], JArrayFrame[U]) = {
    (JArrayFrame(rs.map(_ r2), jar1), JArrayFrame(rs.map(_ r3), jar2))
  }
}

class JArrayFrame[T <% JArrayType : Manifest] private(val frameLevels: List[Int], val jar: JArray[T]) {
  val frames = {
    var tempShape = jar.shape
    (for (rank <- frameLevels.reverse) yield {
    	val frame = tempShape.take(tempShape.length - rank)
    	tempShape = tempShape.drop(tempShape.length - rank)
    	frame
    }).toList :+ tempShape
  }
  val cellShape = frames.last
  val cellSize = cellShape.foldLeft(1)(_ * _)
  val frameSize = jar.shape.foldLeft(1)(_ * _) / cellSize

  	def mapOnCells[R <% JArrayType : Manifest](func: JArray[T] => JArray[R]): JArray[R] = {

  	  val newCells = (for (fr <- 0 until frameSize) yield {
  	    func(JArray(jar.jaType, cellShape, jar.ravel.slice(fr*cellSize, (1+fr)*cellSize)))
  	  })
  	  val newShape = frames.dropRight(1).foldLeft(List[Int]())(_ ++ _) ++ newCells(0).shape
  	  JArray(newCells(0).jaType, newShape, newCells.foldLeft(Vector[R]())(_ ++ _.ravel) )
  	  
  	}
  
  def mapOnCells[U <% JArrayType : Manifest, R <% JArrayType : Manifest](
      func: (JArray[T], JArray[U]) => JArray[R], other: JArrayFrame[R]) = {
  		val resShapeAgree = this.shapeAgreement(other)
  		resShapeAgree match {
  		  case None => throw new Exception() //TODO shape error
  		  case Some(sv) => {
  		    
  		  }
  		}
  }
  
  	def shapeAgreement(other: JArrayFrame[_]):Option[List[List[Int]]] = {
  	  if (this.frames.length != other.frames.length) None
  	  else {
  	    (this.frames, other.frames).zipped.map((l1, l2) => {
  	      if (l1.length > l2.length) {
  	        if (l1.take(l2.length) == l2) Some(l1)
  	        else None
  	      }
  	      else {
  	        if (l2.take(l1.length) == l1) Some(l2)
  	        else None
  	      }
  	    }).foldLeft(Option(List[List[Int]]()))( (o,l) => {
  	      o match {
  	        case None => None
  	        case Some(lp) => l match {
  	          case None => None
  	          case Some(ln) => Some(lp :+ ln)
  	        }
  	      }
  	    })
  	  }
  	}
  
  	def shapeToNewFrame(newFrame: List[List[Int]]) = {
  	  val howManyToRight = this.frames.map(_.foldLeft(1)(_ * _)).scanRight(1)(_ * _)
  	  val howManyToLeft  = newFrame.map(_.foldLeft(1)(_ * _)).scanLeft(1)(_ * _)
  	  println("Old frame: " + this.frames)
  	  println("Desired frame: " + newFrame)
  	  def helper(ofs: List[List[Int]], nfs: List[List[Int]], hmrs: List[Int], hmls: List[Int], acc: Vector[T]):Vector[T] = {
  	    println("acc is: " + acc)
  	    if (ofs.isEmpty) acc
  	    else {
  	      val (of, nf, hmr, hml) = (ofs.head, nfs.head, hmrs.head, hmls.head)
  	      if (of.length == nf.length)
  	        helper(ofs.drop(1), nfs.drop(1), hmrs.drop(1), hmls.drop(1), acc)
  	      else {
  	        val numCopies = nf.foldLeft(1)(_ * _) / of.foldLeft(1)(_ * _)
  	        println("num copies: " + numCopies)
  	        val newacc = (0 until hml).foldLeft(Vector[T]())((vec, i) => {
  	        	vec ++ (0 until numCopies).map( (k: Int) => {
  	        	    (0 until hmr).map(
  	        	        (j: Int) => acc(j + i*hmr))
  	        	 }).foldLeft(Vector[T]())(_ ++ _)
//  	          vec ++ ((0 until numCopies).map((0 until hmr).map((j: Int) => {
//  	            println("Index " + (j + i*hmr) + " of acc is " + acc(j + i*hmr))
//  	            acc(j + (i*hmr))
//  	          } ) ))
  	        })
  	        val newRavel = helper(ofs.drop(1), nfs.drop(1), hmrs.drop(1), hmls.drop(1), newacc)
  	        //TODO fix, but the rest works!
  	        new JArrayFrame(newFrame, JArray(jar.jaType, newFrame.fold(List())(_ ++ _)))
  	      }
  	    }
  	  }
  	  
  	  helper(this.frames, newFrame, howManyToRight, howManyToLeft, jar.ravel)
  	}
  	
	override def toString() = {
	  "" + frames + "\n" + jar
	}  
}