package com.hextremelabs.sms.smssender.infobip

import com.hextremelabs.quickee.configuration.Config
import com.hextremelabs.quickee.configuration.Key
import com.hextremelabs.quickee.response.BaseResponse
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.quickee.rest.AbstractHttpClient
import com.hextremelabs.sms.smssender.SmsProvider
import com.hextremelabs.sms.smssender.infobip.dto.SmsDeliveryResponse
import com.hextremelabs.sms.smssender.infobip.dto.SmsServiceRequest
import com.hextremelabs.sms.smssender.infobip.dto.SmsServiceResponse
import java.nio.charset.Charset
import java.util.*
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType.APPLICATION_JSON

/**
 *
 * @author oladeji
 */
@Infobip
@ApplicationScoped
open class InfobipClient : SmsProvider, AbstractHttpClient() {

  @Inject
  @Config
  @Key("sms.infobip.username")
  open var username: String? = null

  @Inject
  @Config
  @Key("sms.infobip.password")
  open var password: String? = null

  @Inject
  @Config
  @Key("sms.infobip.service.url")
  open var serviceBaseUrl: String? = null

  protected open lateinit var authToken: String

  @PostConstruct open fun initAuthToken() {
    authToken = "Basic " + Base64.getEncoder().encodeToString("$username:$password"
        .toByteArray(Charset.forName("UTF-8")))
  }

  override fun sendMessage(phone: String, title: String, message: String): BaseResponse<String> {
    val apiResponse = restClient.target(serviceBaseUrl + "/text/single").request()
        .header("Content-Type", APPLICATION_JSON)
        .header("Accept", APPLICATION_JSON)
        .header("Authorization", authToken)
        .post(Entity.json(SmsServiceRequest(title, phone, message)), SmsServiceResponse::class.java)

    val sentMessage = apiResponse.messages!!.first()
    val groupId = sentMessage.status!!.groupId
    var status = dr.status(if (groupId == 0 || groupId == 1) REQUEST_SUCCESSFUL else TRANSACTION_FAILED)
    return BaseResponse(status, sentMessage.messageId!!)
  }

  override fun isDelivered(messageId: String): Boolean {
    val apiResponse = restClient.target(serviceBaseUrl + "/reports")
        .queryParam("messageId", messageId)
        .request()
        .header("Accept", APPLICATION_JSON)
        .header("Authorization", authToken)
        .get(SmsDeliveryResponse::class.java)

    return apiResponse.results!!.size > 0 && "DELIVERED_TO_HANDSET".equals(apiResponse.results!![0].status!!.name)
  }
}