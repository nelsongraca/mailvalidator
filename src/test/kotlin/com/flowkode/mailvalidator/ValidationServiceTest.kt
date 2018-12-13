package com.flowkode.mailvalidator

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class ValidationServiceTest {

    private lateinit var validationService: ValidationService

    @BeforeTest
    fun beforeTest() {
        //initialize before each test so we have a clean data file
        validationService = ValidationService("who@who.net")
    }

    @Test
    fun simpleString() {
        assertThat(validationService.validateEmail("simpleString"), equalTo(false))
    }

    @Test
    fun simpleAtString() {
        assertThat(validationService.validateEmail("simple@string"), equalTo(false))
    }

    @Test
    fun simpleAtStringDotLocal() {
        assertThat(validationService.validateEmail("simple@string.local"), equalTo(false))
    }

    @Test
    fun uuidAtGmailDotCom() {
        assertThat(validationService.validateEmail(UUID.randomUUID().toString() + "@gmail.com"), equalTo(false))
    }
    @Test
    fun gracanelsonAtGmailDotCom() {
        assertThat(validationService.validateEmail("graca.nelson@gmail.com"), equalTo(true))
    }
}
