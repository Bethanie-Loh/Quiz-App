package com.bethanie.quiz_app.core.di

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat

class ResourceProvider(private val context: Context) {
    fun getColor(colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }

    fun getColorStateList(colorResId: Int): ColorStateList? {
        return ContextCompat.getColorStateList(context, colorResId)
    }

    fun getString(stringResId: Int, vararg formatArgs: Any): String {
        return context.getString(stringResId, *formatArgs)
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clip)
    }
}