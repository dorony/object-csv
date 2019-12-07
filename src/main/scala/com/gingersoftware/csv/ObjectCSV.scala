package com.gingersoftware.csv

import java.io.{FileWriter, Writer}

import com.github.tototoshi.csv._

import scala.reflect.runtime.{universe => ru}

case class Config(header: String = "#",
                  delimiter: Char = defaultCSVFormat.delimiter,
                  quoteChar: Char = defaultCSVFormat.quoteChar,
                  treatEmptyLineAsNil: Boolean = defaultCSVFormat.treatEmptyLineAsNil,
                  escapeChar: Char = defaultCSVFormat.escapeChar,
                  lineTerminator: String = defaultCSVFormat.lineTerminator,
                  quoting: Quoting = defaultCSVFormat.quoting) extends CSVFormat

object ObjectCSV {
  def apply(config: Config = Config()) = new ObjectCSV(config)
}

/**
  * Created by dorony on 01/05/14.
  */
protected class ObjectCSV(config: Config) {
  def readCSV[T: ru.TypeTag](inputPath: String): IndexedSeq[T] = {
    val objectConverter = new ObjectConverter
    val data = CSVReader.open(inputPath)(config).all()
    val header = data.head
    if (!header.head.startsWith(config.header)) {
      throw new Exception(s"Expected a ${config.header} at the start of the header. Found: " + header)
    }
    val headerWithoutComments = Array(header.head.replaceFirst(config.header, "")) ++ header.tail
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
    val csvWriter = CSVWriter.open(writer)(config)
    csvWriter.writeRow(Seq(config.header + headerCapitalized.head) ++ headerCapitalized.tail)
    val rows = objects.view.map(o => converter.fromObject(o, header))
    rows.foreach(r => csvWriter.writeRow(r))
    csvWriter.flush()
    csvWriter.close()
  }

  private def capitalizeFirstLetter(s: String): String = {
    s(0).toUpper + s.substring(1)
  }
}
