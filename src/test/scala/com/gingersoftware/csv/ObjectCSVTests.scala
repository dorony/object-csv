package com.gingersoftware.csv

import java.io.{File, StringWriter}

import com.gingersoftware.csv.ObjectCSV._
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
    val person1 = new Person("Doron,y",10,5.5)
    val person2 = new Person("David",20,6.5)
    writeCSV(IndexedSeq(person1,person2), fileName)
    val peopleFromCSV = readCSV[Person](fileName)
    assert(peopleFromCSV === IndexedSeq(Person("Doron,y",10,5.5),Person("David",20,6.5)))
  }

  test("can write string") {
    val person1 = new Person("Doron,y", 10, 5.5)
    val stringWriter = new StringWriter
    writeCSV(IndexedSeq(person1), stringWriter)
    assert(stringWriter.toString.contains("Doron,y"))
  }
}
