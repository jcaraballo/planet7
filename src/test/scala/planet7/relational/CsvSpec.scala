package planet7.relational

import org.scalatest.WordSpec
import RowSupport._
import CsvSupport._

class CsvSpec extends WordSpec {
  "We can construct a list of Rows from a CSV String" in {
    val data = """
                 |Some,Header,Columns
                 |D,E,F
                 |G,H,I
               """.stripMargin

    assert(Csv(data).rows.head === Row(List(("Some", "D"), ("Header", "E"), ("Columns", "F"))))
  }

  "We handle blank rows" in {
    val data = """
                 |
                 |Some,Header,Columns
                 |
                 |
                 |D,E,F
                 |
                 |G,H,I
                 |
               """.stripMargin

    assert(Csv(data).rows === List(
      Row(List(("Some", "D"), ("Header", "E"), ("Columns", "F"))),
      Row(List(("Some", "G"), ("Header", "H"), ("Columns", "I")))
    ))
  }

  "An empty CSV behaves itself" in {
    assert(Csv("").rows=== Nil)
    assert(Csv("Some,Header,Columns").rows === Nil)
  }
}