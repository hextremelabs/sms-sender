package com.hextremelabs.sms.smssender

import com.hextremelabs.quickee.response.BaseResponse

/**
 *
 * @author oladeji
 */
interface SmsProvider {

  fun sendMessage(phone: String, title: String, message: String) : BaseResponse<String>

  fun isDelivered(messageId: String) : Boolean
}