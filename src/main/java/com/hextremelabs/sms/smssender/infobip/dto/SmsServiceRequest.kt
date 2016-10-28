package com.hextremelabs.sms.smssender.infobip.dto

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Created by oladeji on 11/4/16.
 */
@XmlRootElement
class SmsServiceRequest(
    @get:XmlElement var from: String? = null,
    @get:XmlElement var to: String? = null,
    @get:XmlElement var text: String? = null
)
