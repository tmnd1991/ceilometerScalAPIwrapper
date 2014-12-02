package org.openstack.api.restful.keystone.v2

import java.awt.PageAttributes.MediaType
import java.net.URL
import java.sql.Timestamp
import java.util.Date
import org.eclipse.jetty.client._
import org.eclipse.jetty.client.util.StringContentProvider
import org.eclipse.jetty.http.HttpMethod

import org.openstack.api.restful.keystone.v2.elements.{PasswordCredential, OpenStackCredential}
import org.openstack.api.restful.keystone.v2.requests.TokenPOSTRequest
import org.openstack.api.restful.keystone.v2.responses.TokenResponse

import spray.json._
import spray.json.MyMod._
import org.openstack.api.restful.keystone.v2.requests.JsonConversions._
import org.openstack.api.restful.keystone.v2.responses.JsonConversions._

import scala.collection._

/**
 * @author Antonio Murgia
 * @version 02/11/2014
 */
private class KeystoneTokenProvider(host : URL, tenantName : String,  username : String, password : String)
  extends TokenProvider(host, tenantName,  username, password)
{
  case class TokenInfo(id : String, localIssuedAt : Date, duration : Long)

  private val client = new HttpClient()
  client.setRequestBufferSize(16384)
  client.setResponseBufferSize(32768)

  private val tokens : mutable.Map[Int, TokenInfo] = mutable.Map()

  override def token = {
    val hash = (host.toString+tenantName+username+password).hashCode
    if (!tokens.contains(hash) || isExpired(hash)){
      val tokenInfo = newToken
      tokens(hash) = tokenInfo
    }
    tokens(hash).id
  }

  private def newToken = {
    val a = TokenPOSTRequest(OpenStackCredential(tenantName,PasswordCredential(username,password)))
    val aString = a.toJson.toString

    val url = new URL(host.toString + a.relativeURL).toURI

    val response = client.POST(url).
      header("Content-Type","application/json").
      content(new StringContentProvider(aString)).send()

    val body = response.getContentAsString
    val tokenResponse = body.parseJson.convertTo[TokenResponse]
    new TokenInfo(tokenResponse.access.token.id,
                  new Date(),
                  tokenResponse.access.token.expires.getTime - tokenResponse.access.token.issued_at.getTime)
  }

  private def isExpired(hash : Int) = {
    val tokenInfo = tokens(hash)
    new Date().after(new Date(tokenInfo.localIssuedAt.getTime + tokenInfo.duration))
  }
}

object KeystoneTokenProvider{
  private val providers : mutable.Map[Int,KeystoneTokenProvider] = mutable.Map()

  def getInstance(host : URL, tenantName : String,  username : String, password : String) : TokenProvider = {
    val hashed = (host.toString + tenantName + username + password).hashCode
    if (providers.contains(hashed)){
      providers(hashed)
    }
    else{
      val newProvider = new KeystoneTokenProvider(host, tenantName,  username, password)
      providers(hashed) = newProvider
      newProvider
    }
  }
}