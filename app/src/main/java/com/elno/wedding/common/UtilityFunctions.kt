package com.elno.wedding.common

import android.content.Context
import android.content.res.Resources
import com.elno.wedding.R
import com.google.gson.Gson
import java.lang.Exception
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

    fun getLocalizedTextFromJsonString(context: Context?, jsonString: String?): String? {
        return try {
            val gson = Gson()
            val map: Map<String, String>? = gson.fromJson(jsonString, Map::class.java) as? Map<String, String>
            getLocalizedTextFromMap(context, map)
        } catch(e: Exception) {
            context?.getString(R.string.empty)
        }

    }

    fun getLocalizedTextFromMap(context: Context?, map: Map<String, String>?): String? {
        val langString: String = LocaleManager(context).getLanguage()
        return map?.get(langString).orEmpty()
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