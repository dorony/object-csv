package com.gingersoftware.csv

import java.io.{FileWriter, Writer}

import com.github.tototoshi.csv.{CSVReader, CSVWriter}

import scala.reflect.runtime.{universe => ru}

/**
 * Created by dorony on 01/05/14.
 */
object ObjectCSV {
  def readCSV[T: ru.TypeTag](inputPath: String): IndexedSeq[T] = {
    val objectConverter = new ObjectConverter
    val csvReader = CSVReader.open(inputPath)
    val data = csvReader.all()
    val header = data.head
    if (!header.head.startsWith("#")) {
      throw new Exception("Expected a commented out header. Found: " + header)
    }
    val headerWithoutComments = Array(header.head.substring(1)) ++ header.tail
    val objects = data.view.tail.map(row => objectConverter.toObject[T](row, headerWithoutComments))
    objects.toVector
  }

  def writeCSV[T <: Product : ru.TypeTag](objects: Seq[T], outputPath: String) {
    writeCSV(objects, new FileWriter(outputPath))
  }

  def writeCSV[T <: Product : ru.TypeTag](objects: Seq[T], writer: Writer) {
    val converter = new ObjectConverter
    val header = converter.getHeader[T]()
    val headerCapitalized = header.map(capitalizeFirstLetter)
    val csvWriter = CSVWriter.open(writer)
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
