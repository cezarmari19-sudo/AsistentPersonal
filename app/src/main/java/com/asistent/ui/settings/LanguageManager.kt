package com.asistent.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageManager {
    data class Language(val code: String, val label: String, val flag: String)
    val all = listOf(Language("en","English","🇬🇧"), Language("ro","Română","🇷🇴"), Language("de","Deutsch","🇩🇪"), Language("ru","Русский","🇷🇺"))
    fun set(code: String) = AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
    fun current(): String = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore("-").ifBlank { "en" }
}
