object-csv
==========

#Usage

Strongly typed CSV helper for Scala, based on the [scala-csv project](https://github.com/tototoshi/scala-csv).
Requires scala 2.11.
To use, add to your build.sbt:
```scala
libraryDependencies += "com.gingersoftware" % "object-csv_2.11" % "0.1"
```

Let’s say you defined this case class:
```scala
case class Person (name: String, age: Int, salary: Double, isNice:Boolean = false)
```

You can write a collection of Person to a .csv file this way:

```scala
import com.gingersoftware.csv.ObjectCSV._
val person1 = new Person("Doron,y",10,5.5)
val person2 = new Person("David",20,6.5)
writeCSV(IndexedSeq(person1,person2), fileName)
```

This will generate the following CSV file:

``` 
#Name,Age,Salary,IsNice
"Doron,y",10,5.5,false 
David,20,6.5,false
```
 
In a similar manner, you can also read this CSV file as a collection of Person:

```scala 
val peopleFromCSV = readCSV[Person](fileName)
assert(peopleFromCSV === IndexedSeq(Person("Doron,y",10,5.5),Person("David",20,6.5)))
```

The order of the columns in the CSV file doesn't matter, we use the header to match each value to the correct constructor argument.


#Limitations

1) It only works with Scala 2.11, as it uses scala.reflect which wasn’t really stable on 2.10. So make sure you have set scalaVersion := "2.11.0" in your build.sbt.

2) It only works with case classes, as all the reflection stuff is based on using your case class primary constructor.

3) For reading, we only currently support the following data types: Int, Double, Boolean and String. We’ll probably add more as we need them. Writing works with everything, as we just .toString it all.

4) We can only read/write CSV files with headers, and the header must begin with the comment mark (#).

5) The API currently doesn’t expose ways to control the type of separator used in the CSV file, but it is very easy to add (the scala-csv project does support it).

6) We didn’t test it for speed, reading is likely to be slow as it uses reflection heavily.
