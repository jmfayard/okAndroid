
----

# Android Resouces Naming Convention

I follow a general rule for naming android layouts, ids, strings.

> WHERE_WHAT_DESCRIPTION

So for example this belong together

- classes `RegisterView` and `RegisterScreen`
- layout `R.layout.register_screen`
- ... which use ids like `register_label_login`, `register_edit_phone`, `register_button_sms`
- ... and strings with pretty much the same name
- Everything look pretty straightforward from the java side as well, here using data binding:

```kotlin
class RegisterView(context: Context) : BaseScreenView<RegisterScreen>(context) {
    val binding: RegisterScreenBinding = inflate(inflater, this, attach)
    var phoneNumber by binding.registerEditPhone.bindToEditText()
    var onSendSms by binding.registerButtonSendsms.bindToClick()
    var onChooseCountry by binding.registerSpinnerCountry.bindToItemSelected()
}
```


See: [A successful XML naming convention](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)

(I reveresed what and where in the `package by features` spirit)

---------



# Preferences delegates

The [kotlin-jetpack library](https://github.com/nsk-mironov/kotlin-jetpack) allow to bind properties to fragment arguments
or preferences or resources


I used them for the preferences

```md
class Preferences(private val preferences: SharedPreferences) {
    var phoneNumber: String by preferences.bindPreference("", "phone_number")
    var password: String by preferences.bindPreference("")
}
```

The first parameter is the default value. The second the name of the key.
If omitted, the name of the variable is used instead.

------


