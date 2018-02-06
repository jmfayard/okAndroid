package com.github.jmfayard.utils;

import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.RawRes;
import android.support.annotation.XmlRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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