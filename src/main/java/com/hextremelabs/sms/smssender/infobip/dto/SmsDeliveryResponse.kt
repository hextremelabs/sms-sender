package com.hextremelabs.sms.smssender.infobip.dto

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * @author oladeji
 */
@XmlRootElement
class SmsDeliveryResponse(
    @get:XmlElement(name = "results") var results: List<Result>? = null
)

class Result(
    @get:XmlElement var doneAt: String? = null,
    @get:XmlElement var sentAt: String? = null,
    @get:XmlElement var to: String? = null,
    @get:XmlElement var price: Price? = null,
    @get:XmlElement var error: Error? = null,
    @get:XmlElement var status: Status? = null,
    @get:XmlElement var smsCount: String? = null,
    @get:XmlElement var from: String? = null,
    @get:XmlElement var messageId: String? = null,
    @get:XmlElement var mccMnc: String? = null,
    @get:XmlElement var bulkId: String? = null
)

class Price(
    @get:XmlElement var currency: String? = null,
    @get:XmlElement var pricePerMessage: String? = null
)

class Error(
    @get:XmlElement var id: String? = null,
    @get:XmlElement var groupId: String? = null,
    @get:XmlElement var groupName: String? = null,
    @get:XmlElement var permanent: String? = null,
    @get:XmlElement var description: String? = null,
    @get:XmlElement var name: String? = null
)