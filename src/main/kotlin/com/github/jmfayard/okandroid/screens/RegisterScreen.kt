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
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen

@See(layout = R.layout.register)
class RegisterScreen : Screen<RegisterView>() {

    override fun createView(context: Context)
            = com.github.jmfayard.okandroid.screens.RegisterView(context)

    override fun getTitle(context: Context?): String
            = context!!.getString(com.github.jmfayard.okandroid.R.string.register_phone)

    fun enterVerification(country: String, phoneNumber: String) {
        navigator.goTo(VerificationScreen())
    }

    override fun onResume(context: Context?) {
        view.registerClicks()
    }

    override fun onPause(context: Context?) {
        view.removeClicks()
    }

}

class RegisterView(context: Context) : BaseScreenView<RegisterScreen>(context) {
    val binding : RegisterBinding = inflate(inflater, this, attach)

    fun registerClicks() {
        binding.sendSms.setOnClickListener {
            screen.enterVerification(country!!, phoneNumber())
        }
        binding.chooseCountry.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                country = null
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                country = binding.chooseCountry.adapter.getItem(position) as String
            }

        }
    }

    private fun phoneNumber(): String
            = binding.phone.text.toString()


    var country: String? = null


    fun removeClicks() {
        listOf(binding.sendSms).forEach { view ->
            view.setOnClickListener(null)
        }
    }
}