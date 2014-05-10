package com.gingersoftware.csv

import org.scalatest.FunSuite
import com.gingersoftware.csv.ObjectCSV._
import java.io.File

/**
 * Created by dorony on 01/05/14.
 */
class ObjectCSVTests extends FunSuite {
  val fileName = "test_csv.csv"
  test("can write and read csv file") {
    val file = new File(fileName)
    if (file.exists())
      file.delete()
    val person1 = new Person("Doron,y",10,5.5)
    val person2 = new Person("David",20,6.5)
    writeCSV(IndexedSeq(person1,person2), fileName)
    val peopleFromCSV = readCSV[Person](fileName)
    assert(peopleFromCSV === IndexedSeq(Person("Doron,y",10,5.5),Person("David",20,6.5)))
  }
}
