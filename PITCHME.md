
# OkAndroid

I experiment with the good new stuff that came to Android in the recent years inside this project

https://github.com/jmfayard/okAndroid

I document here briefly the results of my experiments

Visit this URL to view them as slides

https://gitpitch.com/jmfayard/okandroid/master


---

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

---

# Ditching activities and fragments with Magellan

The Magellan library is based on the following principles

- A single activity is used
- We provide magellans for each screen a plain object called a Screen that survives rotation and everything
- A screen has a method to create an (android) View, which magellan calls when there is a rotation or anything
- In MVP terms,the screen is the P and the view is the V
- Magellan provides an navigator between screens and views

Simplest example

```kotlin
class DetailScreen : Screen<DetailView>() {

    override fun createView(context: Context): DetailView = DetailView(context)

    override fun getTitle(context: Context): String = context.getString(R.string.detail_title)

}

class DetailView (context: Context) : BaseScreenView<DetailScreen>(context) {
    init {
        inflateViewFrom(R.layout.detail_screen)
    }
}
```

Links

- Magellan, the simplest navigation library for Android. https://github.com/wealthfront/magellan
- [Modern Android: Ditching Activities and Fragments](https://news.realm.io/news/sf-fabien-davos-modern-android-ditching-activities-fragments/)


---


# Preferences delegates

[Kotlin Delegated Properties](https://kotlinlang.org/docs/reference/delegated-properties.html) is a powerful feature that is used the [kotlin-jetpack library](https://github.com/nsk-mironov/kotlin-jetpack) allow to bind properties to fragment arguments
or preferences or resources

```md
class Preferences(private val preferences: SharedPreferences) {
    var phoneNumber: String by preferences.bindPreference("", "phone_number")
    var password: String by preferences.bindPreference("")
}
```


The first parameter is the default value. The second the name of the key.
If omitted, the name of the variable is used instead.


---

# Binding Views

Three ways to bind android views in our classes

1. Using [ButterKnife for Kotlin](https://github.com/JakeWharton/kotterknife/blob/master/src/main/kotlin/kotterknife/ButterKnife.kt)
2. Using android data-binding - but without the `data` part. Just wrap the layout inside `<layout>`
3. Abstract android views leveraging [Kotlin Delegated Properties](https://kotlinlang.org/docs/reference/delegated-properties.html) as in this [brillantly simple proposal introducing KotlinAndroidViewBindings](http://marcinmoskala.com/android/kotlin/2017/05/05/still-mvp-or-already-mvvm.html)

The first gives us an easy path of migration for when the project is already using Butterknife

The second and third options combine very well together

```kotlin
class RegisterView(context: Context) : BaseScreenView<RegisterScreen>(context) {
    val binding: RegisterScreenBinding = inflate(inflater, this, attach)
    var phoneNumber : String by binding.registerEditPhone.bindToEditText()
    var onSendSms by binding.registerButtonSendsms.bindToClick()
    var onChooseCountry by binding.registerSpinnerCountry.bindToItemSelected()
}
```


---



# RegisterView without the boilerplate

The `SlimAdapter` library let us avoid the boilerplate that plagues `RecyclerView`, especially when it has to deal with multiple types of items.


```kotlin
class HomeView(context: android.content.Context) : BaseScreenView<HomeScreen>(context) {

    val list: RecyclerView by bindView(R.id.recycler)

    val slimAdapter by lazy {
        SlimAdapter.create()
                .register<HomeItem>(R.layout.home_item_card) { data: HomeItem, injector ->
                    injector.text(R.id.home_item_title, data.title)
                            .text(R.id.home_item_description, data.description)
                            .clicked(R.id.card_view, { _ -> screen.onItemClicked(data) })
                }
                .register<HeaderItem>(R.layout.home_item_section) { item: HeaderItem, injector ->
                    injector.text(R.id.home_item_title, item.title)
                }
                .attachTo(list)
    }

    init {
        inflateViewFrom(R.layout.home_screen)
        with(list) {
            layoutManager = LinearLayoutManager(context)
            adapter = slimAdapter
        }
    }
}
```

Links: https://github.com/MEiDIK/SlimAdapter



