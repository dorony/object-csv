package com.gingersoftware.csv

import java.io.{BufferedWriter, File, FileWriter, StringWriter}

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
    val person1 = Person("Doron,y", 10, 5.5)
    val person2 = Person("David", 20, 6.5)
    val objectCsv = ObjectCSV()
    objectCsv.writeCSV(IndexedSeq(person1, person2), fileName)
    val peopleFromCSV = objectCsv.readCSV[Person](fileName)
    assert(peopleFromCSV === IndexedSeq(Person("Doron,y", 10, 5.5), Person("David", 20, 6.5)))
  }

  test("can write and read csv file with options") {
    val fileName = "test_csv.csv"
    val file = new File(fileName)
    if (file.exists()) {
      file.delete()
    }
    val person1 = PersonWithOptions(Some("Doron,y"), Some(10), Some(5.5), Some(true))
    val person2 = PersonWithOptions(Some("David"), Some(20), Some(6.5), Some(true))
    val objectCsv = ObjectCSV()
    objectCsv.writeCSV(IndexedSeq(person1, person2), fileName)
    val peopleFromCSV = objectCsv.readCSV[PersonWithOptions](fileName)
    assert(peopleFromCSV === IndexedSeq(PersonWithOptions(Some("Doron,y"), Some(10), Some(5.5), Some(true)), PersonWithOptions(Some("David"), Some(20), Some(6.5), Some(true))))
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

  test("can read a csv file the header not commented out") {
    val file = new File("test.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("name,age,salary,isNice\n\"Doron,y\",10,5.5")
    bw.close()
    val person = ObjectCSV(Config(header = "", delimiter = ',', lineTerminator = "$")).readCSV[Person](file.getCanonicalPath)
    file.delete()
    assert(person.head.name == "Doron,y")
    assert(person.head.age == 10)
    assert(person.head.salary == 5.5)
    assert(!person.head.isNice)
  }

  test("can read in a csv file the header commented out") {
    val file = new File("test.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("#name,age,salary,isNice\n\"Doron,y\",10,5.5")
    bw.close()
    val person = ObjectCSV(Config(header = "#", delimiter = ',', lineTerminator = "$")).readCSV[Person](file.getCanonicalPath)
    file.delete()
    assert(person.head.name == "Doron,y")
    assert(person.head.age == 10)
    assert(person.head.salary == 5.5)
    assert(!person.head.isNice)
  }

  test("can read in a csv file with multiple header starts in the headerline") {
    val file = new File("test.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("Nname,age,salary,isNice\n\"Doron,y\",10,5.5")
    bw.close()
    val person = ObjectCSV(Config(header = "N", delimiter = ',', lineTerminator = "$")).readCSV[Person](file.getCanonicalPath)
    file.delete()
    assert(person.head.name == "Doron,y")
    assert(person.head.age == 10)
    assert(person.head.salary == 5.5)
    assert(!person.head.isNice)
  }

}
