package org.openstack.api.restful.FilterExpressions.ComplexQueryPackage

/**
 * Created by tmnd on 19/10/14.
 */
case class UnaryComplexExpression(o : UnaryComplexOperator, e : Expression) extends ComplexExpression{
  override def toString = o.s + e.toString
}
