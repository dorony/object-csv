package com.ginger.csv

import org.scalatest.FunSuite

/**
 * Created by dorony on 01/05/14.
 */
class ObjectConverterTests extends FunSuite{
  test ("toObject => type with some properties => instantiates the object correctly")  {
    val converter = new ObjectConverter()
    val person = converter.toObject[Person](Array("Doron","30","25.5","true"), Array("name","age","salary","isNice"))
    assert(person === Person("Doron",30,25.5,true))
  }
  test ("toObject => some properties are missing => replaced with default values")  {
    val converter = new ObjectConverter()
    val person = converter.toObject[Person](Array("Doron","30","25.5"), Array("name1","age1","salary1"))
    assert(person === Person(null,0,0))
  }

  test ("toObject => type with some properties => case insensitive")  {
    val converter = new ObjectConverter()
    val person = converter.toObject[Person](Array("Doron","30","25.5"), Array("Name","Age","Salary"))
    assert(person === Person("Doron",30,25.5))
  }

  test ("fromObject => type with some properties => returned in the order of the header")  {
    val person = Person("Doron",30,25.5)
    val converter = new ObjectConverter()
    val result = converter.fromObject(person, Array("salary","name","age"))
    assert (result === IndexedSeq("25.5","Doron","30"))
  }

  test ("fromObject => some properties are missing => replaced with empty string")  {
    val person = Person("Doron",30,25.5)
    val converter = new ObjectConverter()
    val result = converter.fromObject(person, Array("salary","lastName","age"))
    assert (result === IndexedSeq("25.5","","30"))
  }

  test ("fromObject => property is null => use empty string")  {
    val person = Person(null,30,25.5)
    val converter = new ObjectConverter()
    val result = converter.fromObject(person, Array("salary","name","age"))
    assert (result === IndexedSeq("25.5","","30"))
  }

  test ("getHeader => has single ctor => returns the ctor parameter names")  {
    val converter = new ObjectConverter()
    val result = converter.getHeader[Person]
    assert (result === IndexedSeq("name","age","salary","isNice"))
  }

  test ("toObject => type has no constructor => throws")  {
    val converter = new ObjectConverter()
    intercept[IllegalArgumentException] {
      converter.toObject[Product](Array("Doron","30","25.5","true"), Array("name","age","salary","isNice"))
    }
  }

}

case class Person (name: String, age: Int, salary: Double, isNice:Boolean = false)
