package com.elno.wedding.common

import android.content.Context
import android.content.res.Resources
import com.elno.wedding.R
import kotlin.math.roundToInt


object UtilityFunctions {

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density).roundToInt()
    }

    fun dpToPxFloat(context: Context, dp: Int): Float {
        val displayMetrics = context.resources.displayMetrics
        return (dp * displayMetrics.density)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getLocaleText(context: Context, map: Map<String, String>?): String? {
        val langString: String = LocaleManager(context).getLanguage()
        return map?.get(langString)
    }

    fun getType(type: String?): Int {
        return when(type) {
            "all" -> R.string.all
            "show" -> R.string.category_dance_show
            "decoration" -> R.string.category_decoration
            "photography" -> R.string.category_photograph
            else -> R.string.empty
        }
    }
}