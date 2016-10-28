package com.hextremelabs.sms.smssender.infobip

import com.hextremelabs.quickee.response.DefaultResponses
import com.hextremelabs.quickee.response.ResponseCodes
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.quickee.response.ResponseStatus
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author oladeji
 */
class InfobipClientIT {

  companion object {
    val client = InfobipClient()
    val phone = System.getProperty("phone")

    @BeforeClass @JvmStatic fun setup() {
      client.username = System.getProperty("infobipUsername")
      client.password = System.getProperty("infobipPassword")
      println("USERNAME_FOUND: ${client.username != null}\nPASSWORD_FOUND: ${client.password != null}")
      client.serviceBaseUrl = "https://api.infobip.com/sms/1"

      client.dr = Mockito.mock(DefaultResponses::class.java).apply {
        `when`(status(REQUEST_SUCCESSFUL)).thenReturn(ResponseStatus(REQUEST_SUCCESSFUL, "Request Successful"))
        `when`(status(TRANSACTION_FAILED)).thenReturn(ResponseStatus(TRANSACTION_FAILED, "Unable to send text message"))
      }
      client.setup()
      client.initAuthToken()
    }
  }

  @Test
  fun sendMessageAndIsDelivered() {
    println("testSendMessage")
    var apiResponse = client.sendMessage(phone, "Lite IT", "Integration test. InfobipClient#sendMessage()")
    assertTrue { apiResponse.status.code == ResponseCodes.REQUEST_SUCCESSFUL }
    assertNotNull(apiResponse.entity)

    println("testIsDelivered")
    client.isDelivered(apiResponse.entity)
  }
}