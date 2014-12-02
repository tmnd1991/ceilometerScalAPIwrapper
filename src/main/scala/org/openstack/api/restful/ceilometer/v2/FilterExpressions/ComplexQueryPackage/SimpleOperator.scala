package org.openstack.api.restful.ceilometer.v2.FilterExpressions.ComplexQueryPackage

/**
 * @author Antonio Murgia
 * @version 18/10/14
 * ceilometer SimpleOperator representation
 * as explained at http://wiki.openstack.org/wiki/Ceilometer/ComplexFilterExpressionsInAPIQueries
 */
abstract class SimpleOperator(val s : String){
}
object SimpleOperator{
  val values = Map(Equals.s -> Equals,
                   NotEquals.s -> NotEquals,
                   GreaterThan.s -> GreaterThan,
                   GreaterOrEqualsThan.s -> GreaterOrEqualsThan,
                   LesserThan.s -> LesserThan,
                   LesserOrEqualsThan.s -> LesserOrEqualsThan)
  object Equals extends SimpleOperator("=")
  object NotEquals extends SimpleOperator("!=")
  object GreaterThan extends SimpleOperator(">")
  object GreaterOrEqualsThan extends SimpleOperator(">=")
  object LesserThan extends SimpleOperator("<")
  object LesserOrEqualsThan extends SimpleOperator("<=")
}