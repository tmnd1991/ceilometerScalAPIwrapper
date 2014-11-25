package org.openstack.api.restful.keystone.v2.elements

import java.net.URL

/**
 * Created by tmnd on 09/11/14.
 */
case class OpenStackUser(username : String,
                         roles_links : Seq[URL],
                         id : String,
                         roles : Seq[OpenStackRole],
                         name : String)
