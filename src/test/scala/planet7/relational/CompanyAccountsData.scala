package planet7.relational

import scala.io.Source
import planet7.relational.CsvSupport.Csv
import planet7.relational.RowSupport.Row

object CompanyAccountsData {
  def readFile(name: String) = Source.fromFile(s"src/test/resources/planet7/relational/csv/$name").getLines().mkString("\n")
  def postcodeLookupTable = Map(Csv(readFile("postcodes.csv")).rows map toTuple:_*)
  private def toTuple(row: Row): (String, String) = row.values match {
    case beforeValue :: afterValue :: Nil => beforeValue._2 -> afterValue._2
    case _ => ???
  }
}