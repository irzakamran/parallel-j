package j.lang.datatypes

<<<<<<< HEAD
import j.lang.datatypes.array.types.JNumberTypes._


object JFuncRank {
	def apply[N <% JNumber](r1: N, r2: N, r3: N) = new JFuncRank(r1,r2,r3)
}

class JFuncRank(r1: JNumber, r2: JNumber, r3: JNumber)
=======
object JFuncRank {
	def apply[N <% JNumber](r1: N, r2: N, r3: N) = new JFuncRank(r1,r2,r3)
	def apply[N <% JNumber](r: N) = new JFuncRank(r, r, r)
}

class JFuncRank(r1: JNumber, r2: JNumber, r3: JNumber) //TODO extend to type which includes infinity
>>>>>>> 45a352e824957fefc57da57c35dd1a2392d8d365
