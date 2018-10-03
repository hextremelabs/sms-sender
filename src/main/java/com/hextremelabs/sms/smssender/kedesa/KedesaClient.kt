package com.hextremelabs.sms.smssender.kedesa

import com.hextremelabs.quickee.configuration.Config
import com.hextremelabs.quickee.response.BaseResponse
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.quickee.rest.AbstractHttpClient
import com.hextremelabs.sms.smssender.SmsProvider
import com.hextremelabs.sms.smssender.kedesa.dto.SmsServiceResponse
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 *
 * @author oladeji
 */
@Kedesa
@ApplicationScoped
class KedesaClient : SmsProvider, AbstractHttpClient() {

  @Inject
  @Config("sms.kedesa.api.key")
  internal lateinit var apiKey: String

  @Inject
  @Config("sms.kedesa.api.secret")
  internal lateinit var apiSecret: String

  @Inject
  @Config("sms.kedesa.service.url")
  internal lateinit var serviceBaseUrl: String

  override fun sendMessage(phone: String, title: String, message: String): BaseResponse<String> {
    val rawResponse = restClient.target("$serviceBaseUrl/sendsms")
        .queryParam("api_key", apiKey)
        .queryParam("api_secret", apiSecret)
        .queryParam("destination", if (phone.startsWith("+")) phone.substring(1) else phone)
        .queryParam("source", title)
        .queryParam("message", message)
        .request()
        .get(String::class.java)

    val tokens = rawResponse.split("|")
    val apiResponse = SmsServiceResponse(tokens[0], tokens[1], tokens[2])
    return BaseResponse(dr.status(if ("1701" == apiResponse.status) REQUEST_SUCCESSFUL else TRANSACTION_FAILED),
        apiResponse.messageId!!)
  }

  override fun isDelivered(messageId: String): Boolean {
    val apiResponse = restClient.target("$serviceBaseUrl/getsmsstatus")
        .path(messageId)
        .request()
        .get(String::class.java)

    return apiResponse.contains("DELIVRD", true)
  }
}
