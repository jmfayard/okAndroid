package com.github.jmfayard.okandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.github.jmfayard.okandroid.databinding.HomeBinding
import com.github.jmfayard.okandroid.phone.RegisterScreen
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.transitions.CircularRevealTransition

class HomeScreen : Screen<HomeView>() {

    override fun createView(context: Context): HomeView {
        return HomeView(context)
    }

    override fun getTitle(context: Context?): String {
        return "Home Screen"
    }

    fun defaultTransitionButtonClicked() {
        navigator.goTo(DetailScreen())
    }

    fun circularRevealTransitionButtonClicked(clickedView: View) {
        navigator.overrideTransition(CircularRevealTransition(clickedView)).goTo(DetailScreen())
    }

    fun showTransitionButtonClicked() {
        navigator.show(DetailScreen())
    }

    fun showNowTransitionButtonClicked() {
        navigator.showNow(DetailScreen())
    }

    fun openRegisterScreen() {
        navigator.goTo(RegisterScreen())
    }

    fun showAirbnbViews() {
        navigator.goTo(AirbnbScreen())
    }

    fun showRxPlayground() {
        navigator.goTo(RxScreen())
    }

    fun showAndroidFeatures() {
        navigator.goTo(AndroidFeaturesScreen())
    }
}

class HomeView (context: Context) : BaseScreenView<HomeScreen>(context) {

    val binding = HomeBinding.inflate(LayoutInflater.from(context), this, true)

    init {

        binding.actionRegister.setOnClickListener { screen.openRegisterScreen() }
        binding.defaultTransitionButton.setOnClickListener { screen.defaultTransitionButtonClicked() }
        binding.circularRevealTransitionButton.setOnClickListener { screen.circularRevealTransitionButtonClicked(binding.circularRevealTransitionButton) }
        binding.showTransitionButton.setOnClickListener { screen.showTransitionButtonClicked() }
        binding.showNowTransitionButton.setOnClickListener { screen.showNowTransitionButtonClicked() }
        binding.actionAirbnb.setOnClickListener { screen.showAirbnbViews() }
        binding.rxBinding.setOnClickListener { screen.showRxPlayground() }
        binding.androidFeatures.setOnClickListener { screen.showAndroidFeatures() }


    }

}