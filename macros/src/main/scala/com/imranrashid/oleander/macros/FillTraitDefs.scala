package com.imranrashid.oleander.macros

import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.annotation.StaticAnnotation

class FillTraitDefs extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro SimpleTraitImpl.addDefs
}

class AddTraitAsSuper extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro SimpleTraitImpl.addDefsAndTrait
}

trait SimpleTrait {
  def x: Int
  def y: Float
}

object SimpleTraitImpl {

  def addDefs(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    impl(c)(false, annottees: _*)
  }

  def addDefsAndTrait(c:Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    impl(c)(true, annottees: _*)
  }

  def impl(c: Context)(addSuper: Boolean, annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val inputs = annottees.map(_.tree).toList
    val newDefDefs = List(
      DefDef(Modifiers(), newTermName("x"), List(), List(), TypeTree(), Literal(Constant(5))),
      DefDef(Modifiers(), newTermName("y"), List(), List(), TypeTree(), Literal(Constant(7.0f)))
    )
    if(addSuper) ???
//    val addedTrait = TypeTree().setOriginal(AppliedTypeTree(Select(Select(Ident("java"), "java.util"), "java.util.Map")))
    val modDefs = inputs map {tree => tree match {
      case ClassDef(mods, name, something, template) =>
        val q = template match {
          case Template(superMaybe, emptyValDef, defs) =>
            Template(superMaybe, emptyValDef, defs ++ newDefDefs)
          case y =>
            y
        }
        ClassDef(mods, name, something, q)
      case x =>
        x
    }}
    val result = c.Expr(Block(modDefs, Literal(Constant())))
    result
  }
}
