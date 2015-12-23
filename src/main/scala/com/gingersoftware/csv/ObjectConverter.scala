package com.gingersoftware.csv
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
      case Some(v) if v.isEmpty => getDefault(paramType)
      case Some(v) => convert(v, paramType)
    }
   }

  private def convert(v: String, t: ru.Type): Any = {
    t match {
      case _ if t =:= ru.typeOf[Int] => v.toInt
      case _ if t =:= ru.typeOf[Long] => v.toLong
      case _ if t =:= ru.typeOf[BigDecimal] => BigDecimal(v)
      case _ if t =:= ru.typeOf[Double] => v.toDouble
      case _ if t =:= ru.typeOf[Boolean] => v.toBoolean
      case _ if t =:= ru.typeOf[Option[Int]] => Some(v.toInt)
      case _ if t =:= ru.typeOf[Option[Double]] => Some(v.toDouble)
      case _ if t =:= ru.typeOf[Option[Boolean]] => Some(v.toBoolean)
      case _ if t =:= ru.typeOf[Option[Long]] => Some(v.toLong)
      case _ if t =:= ru.typeOf[Option[BigDecimal]] => Some(BigDecimal(v))
      case _ if t =:= ru.typeOf[Option[String]] => Some(v)
      case _ => v
    }
  }

  private def getDefault(paramType: ru.Type) : Any = {
    paramType match {
      case _ if paramType =:= ru.typeOf[Int] => 0
      case _ if paramType =:= ru.typeOf[Double] => 0
      case _ if paramType =:= ru.typeOf[Boolean] => false
      case _ if paramType =:= ru.typeOf[Option[Int]] => None
      case _ if paramType =:= ru.typeOf[Option[Double]] => None
      case _ if paramType =:= ru.typeOf[Option[Boolean]] => None
      case _ if paramType =:= ru.typeOf[Option[String]] => None
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
      if (value.isEmpty || value.get == null) ""
      else value.get match {
        case Some(v) => v.toString
        case None => ""
        case v => v.toString
      }
    })
  }
  def getHeader[T: ru.TypeTag]() : IndexedSeq[String] = {
    val (_, parametersAndType) = getCtorAndParametrs[T]
    parametersAndType.view.map{case (name,pType) => name}.toVector
  }
}
