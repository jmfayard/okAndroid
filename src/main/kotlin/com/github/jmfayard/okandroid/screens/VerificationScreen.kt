package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.databinding.VerifyBinding
import com.github.jmfayard.okandroid.databinding.VerifyBinding.inflate
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.toast
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

class VerificationScreen : Screen<VerificationView>() {

    override fun getTitle(context: Context?): String {
        return "Verify your phone number"
    }

    override fun createView(context: Context): VerificationView {
        return VerificationView(context)
    }

    override fun onResume(context: Context?) {
        view.setupClicks()
    }

    override fun onPause(context: Context?) {
        view.removeClicks()
    }

    fun verify(pin: String) {
        if ("123" == pin) {
            view.toast("Success, you are now connected!")
            navigator.goTo(HomeScreen())
        } else {
            view.toast("Error, you did not entered 123!")
        }
    }

    fun verifyResendSMS() {
        navigator.goBack()
    }
}

class VerificationView(context: Context) : BaseScreenView<VerificationScreen>(context) {
    val binding: VerifyBinding = inflate(inflater, this, true)


    fun pin(): String = binding.pin.text.toString()

    fun setupClicks() {
        binding.verifyResendSMS.setOnClickListener { screen.verifyResendSMS() }
        binding.verfiyPin.setOnClickListener { screen.verify(pin()) }
    }

    fun removeClicks() {
        listOf(binding.verfiyPin, binding.verifyResendSMS).forEach { view ->
            view.setOnClickListener(null)
        }
    }
}