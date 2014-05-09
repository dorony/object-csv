package com.ginger.csv

import scala.reflect.runtime.{universe => ru}
import com.github.tototoshi.csv.{CSVWriter, CSVFormat, CSVReader}


/**
 * Created by dorony on 01/05/14.
 */
object ObjectCSV {
  def readCSV[T: ru.TypeTag](inputPath: String): IndexedSeq[T] = {
    val objectConverter = new ObjectConverter
    val csvReader = CSVReader.open(inputPath)
    val data = csvReader.all()
    val header = data.head
    if (!header.head.startsWith("#"))
      throw new Exception("Expected a commented out header. Found: " + header)
    val headerWithoutComments = Array(header.head.substring(1)) ++ header.tail
    val objects = data.view.tail.map(row => objectConverter.toObject[T](row, headerWithoutComments))
    objects.toVector
  }

  def writeCSV[T <: Product : ru.TypeTag](objects: Seq[T], outputPath: String) {
    val converter = new ObjectConverter
    val header = converter.getHeader[T]
    val headerCapitalized = header.map(capitalizeFirstLetter(_))
    val csvWriter = CSVWriter.open(outputPath)
    csvWriter.writeRow(Seq("#" + headerCapitalized.head) ++ headerCapitalized.tail)
    val rows = objects.view.map(o => converter.fromObject(o, header))
    rows.foreach(r => csvWriter.writeRow(r))
    csvWriter.flush()
    csvWriter.close()
  }

  private def capitalizeFirstLetter(s : String) : String = {
    s(0).toUpper + s.substring(1)
  }
}
