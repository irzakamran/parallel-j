package j.lang.datatypes.array.types

import j.lang.datatypes.array.types._
import j.lang.datatypes.array.types.JNumberTypes._

object TypeImplicits {
    //Organized by parameter types
  
    //takes Int (or collections thereof
	implicit def intarray(ar: Array[Int]):Array[JInt] =
	  ar.map(new JInt(_))
	implicit def i2j(i: Int): JInt = new JInt(i)
<<<<<<< HEAD:src/j/lang/datatypes/array/types/TypeImplicits.scala
	
	
=======
	  
>>>>>>> 45a352e824957fefc57da57c35dd1a2392d8d365:src/j/lang/datatypes/array/ArrayImplicits.scala
	implicit def d2j(d: Double): JFloat = new JFloat(d)
	implicit def doubarray(ar: Array[Double]):Array[JFloat] = 
	  ar.map(new JFloat(_))
	
	implicit def c2j(c: Char): JChar = new JChar(c)
	implicit def chararray(ar: Array[Char]): Array[JChar] = 
	  ar.map(new JChar(_))
}