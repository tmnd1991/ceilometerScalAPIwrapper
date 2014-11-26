package org.openstack.api.restful.keystone.v2

import java.net.URL
import java.sql.Timestamp
import java.util.Date

import org.openstack.api.restful.keystone.v2.elements.{PasswordCredential, OpenStackCredential}
import org.openstack.api.restful.keystone.v2.requests.TokenPOSTRequest
import org.openstack.api.restful.keystone.v2.responses.TokenResponse
import org.slf4j.LoggerFactory

import uk.co.bigbeeconsultants.http.header.{Header, Headers, MediaType}
import uk.co.bigbeeconsultants.http.request.RequestBody
import uk.co.bigbeeconsultants.http.url.Href
import uk.co.bigbeeconsultants.http.{Config, HttpClient}

import spray.json._
import org.openstack.api.restful.keystone.v2.requests.JsonConversions._
import org.openstack.api.restful.keystone.v2.responses.JsonConversions._

import scala.collection._

/**
 * Created by tmnd on 10/11/14.
 */
private class KeystoneTokenProvider(host : URL, tenantName : String,  username : String, password : String)
  extends TokenProvider(host, tenantName,  username, password)
{
  case class TokenInfo(id : String, localIssuedAt : Date, duration : Long)

  private val tokens : mutable.Map[Int, TokenInfo] = mutable.Map()

  override def token = {
    val hash = (host.toString+tenantName+username+password).hashCode
    if (!tokens.contains(hash) || isExpired(hash)){
      val tokenInfo = newToken
      tokens(hash) = tokenInfo
    }
    tokens(hash).id
  }
/*
  override def token = {
    val hash = (host.toString+tenantName+username+password).hashCode
    if(!localTokens.contains(hash) || tokenExpirations(hash).after(new Date())){
      val tokenAndExpDate = newToken
      localTokens(hash) = tokenAndExpDate._1
      tokenExpirations(hash) = tokenAndExpDate._2
    }
    localTokens(hash)
  }

  private def newToken = {
    val a = TokenPOSTRequest(OpenStackCredential(tenantName,PasswordCredential(username,password)))
    val aString = a.toJson.toString
    val httpClient = new HttpClient(Config(connectTimeout = 10000,
      readTimeout = 10000,
      followRedirects = false)
    )
    val url = Href(host.toString + a.relativeURL)

    val response = httpClient.post(url.asURL,
      Some(RequestBody(aString,MediaType.APPLICATION_JSON)),
      Headers(Header("Content-type", "application/json"),
        Header("Accept", "application/json"))
    )
    val body = response.body.asString
    val tokenResponse = body.parseJson.convertTo[TokenResponse]
    (tokenResponse.access.token.id, tokenResponse.access.token.expires)
  }
*/
  private def newToken = {
    val a = TokenPOSTRequest(OpenStackCredential(tenantName,PasswordCredential(username,password)))
    val aString = a.toJson.toString
    val httpClient = new HttpClient(Config(connectTimeout = 10000,
      readTimeout = 10000,
      followRedirects = false)
    )
    val url = Href(host.toString + a.relativeURL)

    val response = httpClient.post(url.asURL,
      Some(RequestBody(aString,MediaType.APPLICATION_JSON)),
      Headers(Header("Content-type", "application/json"),
        Header("Accept", "application/json"))
    )
    val body = response.body.asString
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
