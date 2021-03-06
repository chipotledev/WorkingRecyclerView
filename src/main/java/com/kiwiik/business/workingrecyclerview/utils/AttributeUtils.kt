package com.kiwiik.business.workingrecyclerview.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import androidx.databinding.BindingAdapter

/** Utils to show or shoe elements on XML View
 * @author Savir Guev
 * @author https://github.com/chipotledev
 * @version 1.0
 * @since 1.0
 */
class AttributeUtils {

    companion object {
        // Definition for empty images
        private val RESOURCE_EMPTY_IMAGE = -1

        @JvmStatic @BindingAdapter("gone")
        fun gone(view: View, isGone: Boolean) {
            view.visibility = if (isGone) View.GONE else View.VISIBLE
        }

        @JvmStatic @BindingAdapter("invisible")
        fun invisible(view: View, isInvisible: Boolean) {
            view.visibility = if (isInvisible) View.GONE else View.INVISIBLE
        }

    }
}