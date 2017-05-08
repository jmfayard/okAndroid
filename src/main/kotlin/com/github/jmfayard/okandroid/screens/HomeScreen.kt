package com.github.jmfayard.okandroid.screens

import com.github.jmfayard.okandroid.databinding.HomeBinding

class HomeScreen : com.wealthfront.magellan.Screen<HomeView>() {

    override fun createView(context: android.content.Context): HomeView {
        return HomeView(context)
    }

    override fun getTitle(context: android.content.Context?): String {
        return "Home Screen"
    }

    fun defaultTransitionButtonClicked() {
        navigator.goTo(DetailScreen())
    }

    fun circularRevealTransitionButtonClicked(clickedView: android.view.View) {
        navigator.overrideTransition(com.wealthfront.magellan.transitions.CircularRevealTransition(clickedView)).goTo(DetailScreen())
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
        navigator.goTo(TagsScreen())
    }
}

class HomeView (context: android.content.Context) : com.wealthfront.magellan.BaseScreenView<HomeScreen>(context) {

    val binding = HomeBinding.inflate(android.view.LayoutInflater.from(context), this, true)

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