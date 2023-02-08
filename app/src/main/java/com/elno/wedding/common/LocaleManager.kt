package com.elno.wedding.common

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*


class LocaleManager(context: Context?) {

    val prefs: SharedPreferences? = context?.getSharedPreferences("dataFile", Context.MODE_PRIVATE)

    private fun saveLanguage(key: String) {
        prefs?.edit()?.putString("logLanguage", key)?.apply()
    }

    fun getLanguage(): String {
        var lang = Constants.LANGUAGE_KEY_AZ

        if (prefs?.contains("logLanguage") == true)
            lang = prefs.getString("logLanguage", Constants.LANGUAGE_KEY_AZ) ?: Constants.LANGUAGE_KEY_AZ
        return lang
    }

    /**
     * set current pref locale
     * @param mContext
     * @return
     */
    fun setLocale(mContext: Context): Context {
        return updateResources(mContext, getLanguage())
    }

    /**
     * Set new Locale with context
     * @param mContext
     * @param mLocaleKey
     * @return
     */
    fun setNewLocale(mContext: Context, mLocaleKey: String): Context {
        saveLanguage(mLocaleKey)
        return updateResources(mContext, mLocaleKey)
    }

    /**
     * update resource
     * @param context
     * @param language
     * @return
     */
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    /**
     * get current locale
     * @param res
     * @return
     */
    fun getLocale(res: Resources): Locale? {
        val config: Configuration = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales[0] else config.locale
    }

}