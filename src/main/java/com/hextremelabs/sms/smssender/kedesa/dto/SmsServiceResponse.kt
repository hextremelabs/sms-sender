package com.hextremelabs.sms.smssender.kedesa.dto

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 *
 * @author oladeji
 */
@XmlRootElement
class SmsServiceResponse(
    @get:XmlElement var status: String? = null,
    @get:XmlElement var mobile: String? = null,
    @get:XmlElement(name = "message_id") var messageId: String? = null
)
