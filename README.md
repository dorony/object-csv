object-csv
==========
#Updates
- Added support for optional values, Long, BigDecimal.
- Convert empty csv values to default type values
- Scala version updated to 2.11.7

Thanks to a contribution by [aandeers](https://github.com/aandeers) version 0.2 is now available, which allows you to customize the csv delimiters, by passing a Config() object to ObjectCSV. Note that you now have to create an instance of ObjectCSV, as shown below.

#Usage

Strongly typed CSV helper for Scala, based on the [scala-csv project](https://github.com/tototoshi/scala-csv).
Requires scala 2.11.
To use, add to your build.sbt:
```scala
libraryDependencies += "com.gingersoftware" % "object-csv_2.11" % "0.3"
```

Let’s say you defined this case class:
```scala
case class Person (name: String, age: Int, salary: Double, isNice:Boolean = false)
```

You can write a collection of Person to a .csv file this way:

```scala
import com.gingersoftware.csv.ObjectCSV
val person1 = new Person("Doron,y",10,5.5)
val person2 = new Person("David",20,6.5)
ObjectCSV().writeCSV(IndexedSeq(person1,person2), fileName)
```

This will generate the following CSV file:

``` 
#Name,Age,Salary,IsNice
"Doron,y",10,5.5,false 
David,20,6.5,false
```
 
In a similar manner, you can also read this CSV file as a collection of Person:

```scala 
val peopleFromCSV = ObjectCSV().readCSV[Person](fileName)
assert(peopleFromCSV === IndexedSeq(Person("Doron,y",10,5.5),Person("David",20,6.5)))
```

The order of the columns in the CSV file doesn't matter, we use the header to match each value to the correct constructor argument. This allows your files to be more flexible: add columns or change their order, and your code won’t break.


#Limitations

1) It only works with Scala 2.11, as it uses scala.reflect which wasn’t really stable on 2.10. So make sure you have set ```scalaVersion := "2.11.0"``` in your build.sbt.

2) It only works with case classes, as all the reflection stuff is based on using your case class primary constructor.

3) For reading, we only currently support the following data types: Int, Double, Boolean and String. We’ll probably add more as we need them. Writing works with everything, as we just .toString it all.

4) We didn’t test it for speed, reading is likely to be slow as it uses reflection heavily.  

# License
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
