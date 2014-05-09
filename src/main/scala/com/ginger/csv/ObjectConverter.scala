package com.ginger.csv
import scala.reflect.runtime.{universe => ru}

/**
 * Created by dorony on 30/04/14.
 */
class ObjectConverter {
  def toObject[T: ru.TypeTag](data: Seq[String], header: Seq[String]) : T = {
    val propertyToValue = header.view.map(h=>h.toLowerCase).zip(data).toMap
    val (ctorm, parametersAndType) = getCtorAndParametrs[T]
    val args = parametersAndType.map{case (paramName,paramType)=>parseValue(paramName,paramType,propertyToValue)}

    ctorm(args : _*).asInstanceOf[T]
  }

  private def getCtorAndParametrs[T: ru.TypeTag]() :(ru.MethodMirror, List[(String, ru.Type)]) = {
    val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
    val typeObj = ru.typeOf[T]
    val outputClass = typeObj.typeSymbol.asClass
    val cm = runtimeMirror.reflectClass(outputClass)
    val ctorSymbol = typeObj.decl(ru.termNames.CONSTRUCTOR)
    if (ctorSymbol == ru.NoSymbol)
      throw new IllegalArgumentException(s"type $typeObj should have a primary constructor but doesn't")
    val ctor = ctorSymbol.asMethod
    val parametersAndType: List[(String, ru.Type)] = ctor.paramLists.head.map(p => (p.name.decodedName.toString, p.typeSignature))
    val ctorm: ru.MethodMirror = cm.reflectConstructor(ctor)
    (ctorm, parametersAndType)
  }

  private def parseValue(paramName: String, paramType: ru.Type, propertyToValue : Map[String,String]) : Any = {
    propertyToValue.get(paramName.toLowerCase) match {
      case None => getDefault(paramType)
      case Some(v) => convert(v, paramType)
    }
   }

  private def convert(v: String, t: ru.Type): Any = {
    t match {
      case _ if t =:= ru.typeOf[Int] => v.toInt
      case _ if t =:= ru.typeOf[Double] => v.toDouble
      case _ if t =:= ru.typeOf[Boolean] => v.toBoolean
      case _ => v
    }
  }

  private def getDefault(paramType: ru.Type) : Any = {
    paramType match {
      case _ if paramType =:= ru.typeOf[Int] => 0
      case _ if paramType =:= ru.typeOf[Double] => 0
      case _ if paramType =:= ru.typeOf[Boolean] => false
      case _ => null
    }
  }

  private def getCaseClassParams(cc: Product) = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map( _.getName -> values.next ).toMap
  }

  def fromObject(obj: Product, header: IndexedSeq[String]): IndexedSeq[String] = {
    val propToValue = getCaseClassParams(obj)
    header.map(h => {
      val value = propToValue.get(h)
      if (value.isEmpty || value.get == null) "" else value.get.toString
    })
  }
  def getHeader[T: ru.TypeTag]() : IndexedSeq[String] = {
    val (_, parametersAndType) = getCtorAndParametrs[T]
    parametersAndType.view.map{case (name,pType) => name}.toVector
  }
}
