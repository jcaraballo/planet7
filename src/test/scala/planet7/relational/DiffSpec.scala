package planet7.relational

import org.scalatest.WordSpec
import planet7.Diff
import FieldSupport._
import RowSupport._
import CsvSupport._
import scala.io.Source

class DiffSpec extends WordSpec {

  "The result of diffing two CSV lists is a list of rows that are different" in {
    val left = """
                 |ID,Name,Value
                 |A,B,C
                 |D,E,F
                 |G,H,I
               """.stripMargin

    val right = """
                  |ID,Name,Value
                  |A,B,C
                  |G,X,I
                """.stripMargin

    val result: List[(Row, Row)] = Diff(Csv(left).rows, Csv(right).rows, RowDiffer("ID"))

    assert(result === List(
      (Row(List(("ID", "G"), ("Name", "H"), ("Value", "I"))), Row(List(("ID", "G"), ("Name", "X"), ("Value", "I")))),
      (Row(List(("ID", "D"), ("Name", "E"), ("Value", "F"))), RowSupport.EmptyRow)
    ))
  }

  "The result of diffing two rows is a list of field diffs" in {
    val left = List(("ID", "G"), ("Name", "H"), ("Value", "I"))
    val right = List(("ID", "G"), ("Name", "X"), ("Value", "I"))

    val result: List[(Field, Field)] = Diff(left, right, FieldDiffer)

    assert(result === List(("Name", "H") ->("Name", "X")))
  }

  "We know which columns were added and removed" in {
    val left = List(("ID", "G"), ("Name", "H"), ("Removed", "I"))
    val right = List(("Added", "Q"), ("ID", "G"), ("Name", "H"))

    val result: List[(Field, Field)] = Diff(left, right, FieldDiffer)

    assert(result === List(("Removed", "I") -> EmptyField, EmptyField -> ("Added", "Q")))
  }

  "Map CSV files to case classes representing the columns to compare" in {
    case class ComparisonFields(a: String, b: String, d: String, e: Integer)

    object Empty extends ComparisonFields("", "", "", 0) {
      override def toString = "Empty"
    }

    object CfDiffer extends Differentiator[ComparisonFields] {
      def zero = Empty
      def key(u: ComparisonFields) = u.a
    }

    def readFile(name: String) = Source.fromFile(s"src/test/resources/planet7/relational/csv/$name").getLines().mkString("\n")
    def safeInt(s: String): Integer = if (s.isEmpty) 0 else s.toInt
    def createComparison(row: Row) = ComparisonFields(row.value("A"), row.value("B"), row.value("D"), safeInt(row.value("E")))

    val left: List[ComparisonFields] = Csv(readFile("left.csv")).rows map createComparison
    val right: List[ComparisonFields] = Csv(readFile("right.csv")).rows map createComparison

    val result: List[(ComparisonFields, ComparisonFields)] = Diff(left, right, CfDiffer)

    assert(result === List(
      CfDiffer.zero -> ComparisonFields("hjt", "waer", "iughv", 7653),
      ComparisonFields("gfreejuy", "rer", "iu", 642) -> CfDiffer.zero,
      ComparisonFields("", "", "", 0) -> CfDiffer.zero
    ))
  }

  "Map long rows with disparate columns to shorter rows containing just the columns to compare" in {

  }

  // Columns: renamed
  // Results summaries: added, missing, diff (set of column diff counts) ==> all now trivial
  // Identify duplicates in both lists
  // Ability to ignore some columns
  // Ability to set tolerances for numerical field comparisons
}