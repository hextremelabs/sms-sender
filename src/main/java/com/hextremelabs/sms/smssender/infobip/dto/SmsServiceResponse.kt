package com.hextremelabs.sms.smssender.infobip.dto

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * @author oladeji
 */
@XmlRootElement
class SmsServiceResponse(
    @get:XmlElement var messages: List<Message>? = null
)

class Message(
    @get:XmlElement var to: String? = null,
    @get:XmlElement var status: Status? = null,
    @get:XmlElement var smsCount: String? = null,
    @get:XmlElement var messageId: String? = null
)

class Status(
    @get:XmlElement var id: Int? = null,
    @get:XmlElement var name: String? = null,
    @get:XmlElement var groupId: Int? = null,
    @get:XmlElement var groupName: String? = null,
    @get:XmlElement var description: String? = null
)