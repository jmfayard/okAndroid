package com.github.jmfayard.okandroid.phone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.github.jmfayard.okandroid.databinding.RegisterBinding
import com.wealthfront.magellan.BaseScreenView

class RegisterView(context: Context) : BaseScreenView<RegisterScreen>(context) {
    val binding = RegisterBinding.inflate(LayoutInflater.from(context), this, true)

    fun registerClicks() {
        binding.sendSms.setOnClickListener {
            screen.enterVerification(country!!, phoneNumber())
        }
        binding.chooseCountry.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
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


    var country : String? = null


    fun removeClicks() {
        listOf(binding.sendSms).forEach { view ->
            view.setOnClickListener(null)
        }
    }
}
