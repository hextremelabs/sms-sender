package com.hextremelabs.sms.smssender

import com.hextremelabs.quickee.response.BaseResponse
import com.hextremelabs.quickee.response.DefaultResponses
import com.hextremelabs.quickee.response.ResponseCodes.REQUEST_SUCCESSFUL
import com.hextremelabs.quickee.response.ResponseCodes.TRANSACTION_FAILED
import com.hextremelabs.sms.smssender.kedesa.Kedesa
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

 * @author oladeji
 */
@Stateless
@TransactionAttribute(NOT_SUPPORTED)
open class SmsHandler {

  @Inject
  internal open lateinit var dr: DefaultResponses

  @Inject
  @Any
  internal open lateinit var providers: Instance<SmsProvider>

  @Inject
  @Kedesa
  internal open lateinit var default: SmsProvider

  internal open lateinit var preferred: SmsProvider

  protected open val messages = ArrayList<String>()
  protected open val randomGen = Random()

  @PostConstruct
  @Schedule(hour = "*/3", persistent = false)
  open fun resetPreferred() {
    preferred = default;
  }

  @Schedule(hour = "*", minute = "*/5", persistent = false)
  open fun evaluateFailover() {
    if (messages.size < 5) return

    // Choose another smsProvider at random if not up to 60% of randomly selected sent messages were delivered.
    if (Array(5, { preferred.isDelivered(messages[it * (messages.size / 5)]) }).count { it } < 3) failover()
    messages.clear()
  }

  open fun failover() {
    preferred = providerList().filter { it != preferred }[randomGen.nextInt(providerList().count() - 1)]
    messages.clear()
  }

  open fun providerList(): Iterable<SmsProvider> = providers

  @JvmOverloads
  open fun sendSms(phone: String, title: String, message: String, retryCount: Int = 0) : BaseResponse<String> {
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
