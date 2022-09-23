package com.vibs.maashakti.utils

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.util.*

object ViewExtensions {

    /**
     * Show the view  (visibility = View.VISIBLE)
     */
    fun View.show(): View {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        return this
    }

    /**
     * Remove the view (visibility = View.GONE)
     */
    fun View.hide(): View {
        if (visibility != View.GONE) {
            visibility = View.GONE
        }
        return this
    }

    /**
     * Add drawable left to a textview
     *
     * @param DrawableRes
     */
    fun TextView.leftDrawable(@DrawableRes id: Int = 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0)
    }

    /**
     * Add drawable right to a textview
     *
     * @param DrawableRes
     */
    fun TextView.rightDrawable(@DrawableRes id: Int = 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0)
    }

    /**
     * Add drawable left to a textview with size
     *
     * @param DrawableRes
     * @param DimenRes
     */
    fun TextView.leftDrawable(@DrawableRes id: Int = 0, @DimenRes sizeRes: Int) {
        val drawable = ContextCompat.getDrawable(context, id)
        val size = resources.getDimensionPixelSize(sizeRes)
        drawable?.setBounds(0, 0, size, size)
        this.setCompoundDrawables(drawable, null, null, null)
    }

    /**
     * Use to make String title Case
     */
    fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
        it.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
        }
    }
}
