package com.hextremelabs.sms.smssender.kedesa

import com.hextremelabs.quickee.response.DefaultResponses
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.quickee.response.ResponseStatus
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author oladeji
 */
class KedesaClientIT {

  companion object {
    val client = KedesaClient()
    val phone = System.getProperty("phone")

    @BeforeClass @JvmStatic fun setup() {
      client.apiKey = System.getProperty("kedesaApiKey")
      client.apiSecret = System.getProperty("kedesaApiSecret")
      client.serviceBaseUrl = "https://www.kedesa.com"
      client.dr = mock(DefaultResponses::class.java).apply {
        `when`(status(REQUEST_SUCCESSFUL)).thenReturn(ResponseStatus(REQUEST_SUCCESSFUL, "Request Successful"))
        `when`(status(TRANSACTION_FAILED)).thenReturn(ResponseStatus(TRANSACTION_FAILED, "Unable to send text message"))
      }
      client.setup()
    }
  }

  @Test
  fun sendMessageAndIsDelivered() {
    println("testSendMessage")
    val apiResponse = client.sendMessage(phone, "Lite IT", "Integration test. KedesaClient#sendMessage()")
    assertTrue { apiResponse.status.code == REQUEST_SUCCESSFUL }
    assertNotNull(apiResponse.entity)

    println("testIsDelivered")
    client.isDelivered(apiResponse.entity)
  }
}
