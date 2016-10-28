package com.hextremelabs.sms.smssender.kedesa

import javax.inject.Qualifier
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 *
 * @author oladeji
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS, FUNCTION, FIELD, VALUE_PARAMETER)
annotation class Kedesa