package com.github.jmfayard.okandroid;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegisterScreenTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void registerScreenTest() {
        ViewInteraction textView = onView(
                allOf(withText("RegisterScreen"), isDisplayed()));
        textView.check(matches(withText("RegisterScreen")));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.register_spinner_country), isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.register_edit_phone), isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(replaceText("15687"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.register_button_sendsms), withText("Send SMS"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction button = onView(withId(R.id.verfiyPin));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText4 = onView(withId(R.id.pin));
        appCompatEditText4.perform(replaceText("124"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.verfiyPin), isDisplayed()));
        appCompatButton2.perform(click());

        appCompatEditText4.check(matches(isDisplayed()));

        appCompatEditText4.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.verfiyPin), isDisplayed()));
        appCompatButton3.perform(click());


        ViewInteraction textView2 = onView(
                allOf(withId(R.id.home_item_title), withText("RegisterScreen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card_view),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("RegisterScreen")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
