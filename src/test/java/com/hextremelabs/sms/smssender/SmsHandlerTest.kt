package com.hextremelabs.sms.smssender

import com.hextremelabs.quickee.response.BaseResponse
import com.hextremelabs.sms.smssender.infobip.InfobipClient
import com.hextremelabs.sms.smssender.kedesa.KedesaClient
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import javax.enterprise.inject.Instance
import kotlin.test.assertEquals

/**
 * @author oladeji
 */
class SmsHandlerTest {

  companion object {
    val PHONE = "+2348012345678"
  }

  lateinit var handler: SmsHandler
  lateinit var kedesaClient: SmsProvider
  lateinit var infoBipClient: SmsProvider

  @Before
  fun setup() {
    kedesaClient = mock(KedesaClient::class.java)
    infoBipClient = mock(InfobipClient::class.java)

    val providerList = listOf(kedesaClient, infoBipClient).apply {
      forEach {
        `when`(it.sendMessage(PHONE, "Testing Mic", "Testing microphone"))
          .thenReturn(BaseResponse(0, "Request successful", System.currentTimeMillis().toString()))

      `when`(it.sendMessage(PHONE, "Bad Mic", "Testing microphone"))
          .thenReturn(BaseResponse(301, "Failed", System.currentTimeMillis().toString()))
      }
    }

    handler = spy(SmsHandler::class.java).apply {
      default = kedesaClient
      enabledProviders = "Infobip"
      providers = mock(Instance::class.java) as Instance<SmsProvider>
      doReturn(providerList).`when`(this).providerList()
      resetPreferred()
    }
  }

  @Test
  fun testSendSms() {
    println("testSendSms")

    assertEquals(kedesaClient, handler.preferred)

    handler.sendSms(PHONE, "Testing Mic", "Testing microphone")
    verify(handler.preferred, times(1)).sendMessage(PHONE, "Testing Mic", "Testing microphone")
  }

  @Test
  fun testFailover() {
    println("testFailover")

    assertEquals(kedesaClient, handler.preferred)

    handler.failover()
    assertEquals(infoBipClient, handler.preferred)
  }

  @Test
  fun testEvaluateFailover_noFailover() {
    println("testEvaluateFailover_noFailover")

    assertEquals(kedesaClient, handler.preferred)

    repeat(10) { handler.sendSms(PHONE, "Testing Mic", "Testing microphone") }
    `when`(handler.preferred.isDelivered(anyString())).thenReturn(true, true, true, false, false)
    handler.evaluateFailover()
    assertEquals(kedesaClient, handler.preferred)
  }

  @Test
  fun testEvaluateFailover_failover() {
    println("testEvaluateFailover_failover")

    assertEquals(kedesaClient, handler.preferred)

    repeat(10) { handler.sendSms(PHONE, "Testing Mic", "Testing microphone") }
    `when`(handler.preferred.isDelivered(anyString())).thenReturn(true, true, false, false, false)
    handler.evaluateFailover()
    assertEquals(infoBipClient, handler.preferred)
  }

  @Test
  fun testFailoverForFailedMessage() {
    println("testFailoverForFailedMessage")

    assertEquals(kedesaClient, handler.preferred)

    handler.sendSms(PHONE, "Testing Mic", "Testing microphone")
    assertEquals(kedesaClient, handler.preferred)

    handler.sendSms(PHONE, "Bad Mic", "Testing microphone")
    assertEquals(infoBipClient, handler.preferred)
  }
}
