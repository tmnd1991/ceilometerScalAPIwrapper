package org.openstack.api.restful.ceilometer.v2.FilterExpressions.ComplexQueryPackage

/**
 * @author Antonio Murgia
 * @version 18/10/14
 * ceilometer Order representation
 * as explained at http://wiki.openstack.org/wiki/Ceilometer/ComplexFilterExpressionsInAPIQueries
 */
abstract class Order(val s : String)
object Order{
  val values = Map(ASC.s->ASC,
                  DESC.s -> DESC)
  object ASC extends Order("ASC")
  object DESC extends Order("DESC")
}
