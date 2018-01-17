package com.github.jmfayard.okandroid.screens

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

interface HasId {
    val id: Int
}

fun SomeScreen.v(has: HasId): View? =
        this.getActivity()?.findViewById(has.id)

fun SomeScreen.text(has: HasId): TextView? =
        this.getActivity().findViewById(has.id) as? TextView

fun SomeScreen.button(has: HasId): Button? =
        this.getActivity().findViewById(has.id) as? Button

fun SomeScreen.editText(has: HasId): EditText? =
        this.getActivity().findViewById(has.id) as? EditText


fun SomeView.v(has: HasId): View? =
        this.findViewById(has.id)

fun SomeView.text(has: HasId): TextView? =
        this.findViewById(has.id) as? TextView

fun SomeView.editText(has: HasId): EditText? =
        this.findViewById(has.id) as? EditText

fun SomeView.button(has: HasId): Button? =
        this.findViewById(has.id) as? Button


