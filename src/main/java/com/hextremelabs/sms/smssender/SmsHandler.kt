package com.hextremelabs.sms.smssender

import com.hextremelabs.quickee.configuration.Config
import com.hextremelabs.quickee.response.BaseResponse
import com.hextremelabs.quickee.response.DefaultResponses
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.sms.smssender.infobip.Infobip
import java.util.ArrayList
import java.util.Random
import javax.annotation.PostConstruct
import javax.ejb.Schedule
import javax.ejb.Stateless
import javax.ejb.TransactionAttribute
import javax.ejb.TransactionAttributeType.NOT_SUPPORTED
import javax.enterprise.inject.Any
import javax.enterprise.inject.Instance
import javax.inject.Inject

/**
 *
 * @author oladeji
 */
@Stateless
@TransactionAttribute(NOT_SUPPORTED)
class SmsHandler {

  @Inject
  @Config("sms.providers.enabled")
  internal lateinit var enabledProviders: String

  @Inject
  private lateinit var dr: DefaultResponses

  @Inject
  @Any
  internal lateinit var providers: Instance<SmsProvider>

  @Inject
  @Infobip
  internal lateinit var default: SmsProvider

  internal lateinit var preferred: SmsProvider

  private val messages = ArrayList<String>()

  private val randomGen = Random()

  @PostConstruct
  @Schedule(hour = "*/3", persistent = false)
  fun resetPreferred() {
    preferred = default
  }

  @Schedule(hour = "*", minute = "*/5", persistent = false)
  fun evaluateFailover() {
    if (messages.size < 5) return

    // Choose another smsProvider at random if not up to 60% of randomly selected sent messages were delivered.
    val deliveredCountIn5random = Array(5, {
      preferred.isDelivered(messages[it * (messages.size / 5)])
    }).count { it }

    if (deliveredCountIn5random < 3) failover()
    messages.clear()
  }

  fun failover() {
    if (providerList().count() < 2) return

    preferred = providerList().filter { it != preferred }[randomGen.nextInt(providerList().count() - 1)]
    messages.clear()
  }

  fun providerList(): Iterable<SmsProvider> = providers
      .filter { it.javaClass.annotations
          .map { it.javaClass.simpleName }
          .any { enabledProviders.contains(it!!) }
      }

  @JvmOverloads
  fun sendSms(phone: String, title: String, message: String, retryCount: Int = 0) : BaseResponse<String> {
    return try {
      preferred.sendMessage(phone, title, message).apply {
        if (status.code != REQUEST_SUCCESSFUL) failover() else messages.add(entity)
      }
    } catch (ex: Exception) {
      failover()
      return BaseResponse(dr.status(TRANSACTION_FAILED))
    }
  }
}
