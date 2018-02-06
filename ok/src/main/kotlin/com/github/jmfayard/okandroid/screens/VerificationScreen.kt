package com.github.jmfayard.okandroid.screens

import android.content.Context
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.databinding.VerifyScreenBinding
import com.github.jmfayard.okandroid.databinding.VerifyScreenBinding.inflate
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.toast
import com.marcinmoskala.kotlinandroidviewbindings.bindToClick
import com.marcinmoskala.kotlinandroidviewbindings.bindToEditText
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

class VerificationScreen : Screen<VerificationView>() {

    override fun getTitle(context: Context): String = context.getString(R.string.verification_title)


    override fun createView(context: Context) = VerificationView(context)

    override fun onResume(context: Context?) {
        view.onSendSms = { verifyResendSMS() }
        view.onVerifyPin = { verify(view.pin) }
    }

    fun verify(pin: String) {
        if ("123" == pin) {
            toast("Success, you are now connected!")
            navigator.goTo(HomeScreen())
        } else {
            toast("Error, you did not entered 123!")
        }
    }

    fun verifyResendSMS() {
        navigator.goBack()
    }
}

class VerificationView(context: Context) : BaseScreenView<VerificationScreen>(context) {
    val binding: VerifyScreenBinding = inflate(inflater, this, true)
    var pin by binding.pin.bindToEditText()
    var onSendSms by binding.verifyResendSMS.bindToClick()
    var onVerifyPin by binding.verfiyPin.bindToClick()
}