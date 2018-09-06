package com.github.jmfayard.utils;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.RawRes;
import androidx.annotation.XmlRes;

/**
 * Interface used to document that the class depends on
 * <p>
 * - a layout resource
 * - an xml resource
 * - a menu
 * - an animation
 * <p>
 * For example @See(layout = R.layout.activity_main) can be added to MainActivity
 * to document that this resource is used via data_binding
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface See {
    @LayoutRes int layout() default 0;

    @XmlRes int xml() default 0;

    @AnimRes int anim() default 0;

    @MenuRes int menu() default 0;

    @ArrayRes int array() default 0;

    @RawRes int raw() default 0;

    @AnimatorRes int animator() default 0;

    Class<?> java() default Object.class;

}