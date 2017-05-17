package com.github.jmfayard.okandroid.screens

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.attach
import com.github.jmfayard.okandroid.databinding.RegisterBinding
import com.github.jmfayard.okandroid.databinding.RegisterBinding.inflate
import com.github.jmfayard.okandroid.inflater
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.marcinmoskala.kotlinandroidviewbindings.bindToClick
import com.marcinmoskala.kotlinandroidviewbindings.bindToEditText
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotterknife.bindToItemSelected

@See(layout = R.layout.register)
class RegisterScreen : Screen<RegisterView>() {

    override fun createView(context: Context)
            = RegisterView(context)

    override fun getTitle(context: Context): String
            = context.getString(R.string.register_phone)

    fun enterVerification(country: String, phoneNumber: String) {
        navigator.goTo(VerificationScreen())
    }

    var country: String = ""

    override fun onShow(context: Context?) {
        view.onSendSms = {
            enterVerification(country, view.phoneNumber)
        }
        view.onChooseCountry = { position, data ->
            country = data as String
            view.toast("Your country : $country")
        }
    }


}

class RegisterView(context: Context) : BaseScreenView<RegisterScreen>(context) {
    val binding : RegisterBinding = inflate(inflater, this, attach)
    var phoneNumber by binding.phone.bindToEditText()
    var onSendSms by binding.sendSms.bindToClick()
    var onChooseCountry by binding.chooseCountry.bindToItemSelected()
}