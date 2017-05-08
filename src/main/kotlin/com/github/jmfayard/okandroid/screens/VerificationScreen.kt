package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.databinding.VerifyBinding
import com.wealthfront.magellan.BaseScreenView

class VerificationScreen : com.wealthfront.magellan.Screen<VerificationView>() {

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
    val binding: VerifyBinding = VerifyBinding.inflate(android.view.LayoutInflater.from(context), this, true)


    fun pin(): String = binding.pin.text.toString()

    fun toast(s: String) {
        android.widget.Toast.makeText(context, s, android.widget.Toast.LENGTH_LONG).show()
    }

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