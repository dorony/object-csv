package com.gingersoftware.csv

import java.io.{File, StringWriter}

import org.scalatest.FunSuite

/**
 * Created by dorony on 01/05/14.
 */
class ObjectCSVTests extends FunSuite {
  test("can write and read csv file") {
    val fileName = "test_csv.csv"
    val file = new File(fileName)
    if (file.exists()) {
      file.delete()
    }
    val person1 = new Person("Doron,y", 10, 5.5)
    val person2 = new Person("David", 20, 6.5)
    val objectCsv = ObjectCSV()
    objectCsv.writeCSV(IndexedSeq(person1, person2), fileName)
    val peopleFromCSV = objectCsv.readCSV[Person](fileName)
    assert(peopleFromCSV === IndexedSeq(Person("Doron,y", 10, 5.5), Person("David", 20, 6.5)))
  }

  test("can write string") {
    val person = new Person("Doron,y", 10, 5.5)
    val stringWriter = new StringWriter
    ObjectCSV().writeCSV(IndexedSeq(person), stringWriter)
    assert(stringWriter.toString.contains("Doron,y"))
  }

  test("can customize delimiter etc") {
    val person = new Person("Doron,y", 10, 5.5)
    val stringWriter = new StringWriter
    ObjectCSV(Config(header = "//", delimiter = ':', lineTerminator = "$")).writeCSV(IndexedSeq(person), stringWriter)
    val lines = stringWriter.toString.split("\\$")
    assert(lines.length === 2)
    assert(lines(0).startsWith("//"))
    assert(lines(0).count(c => c == ':') === 3)
    assert(lines(1).count(c => c == ':') === 3)
  }
}
