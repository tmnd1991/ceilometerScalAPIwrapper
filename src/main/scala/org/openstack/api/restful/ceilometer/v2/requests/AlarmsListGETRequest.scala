package org.openstack.api.restful.ceilometer.v2.requests

import org.openstack.api.restful.ceilometer.v2.FilterExpressions.Query

/**
 * Created by tmnd on 21/10/14.
 */
case class AlarmsListGETRequest(q : Seq[Query]) {

}
