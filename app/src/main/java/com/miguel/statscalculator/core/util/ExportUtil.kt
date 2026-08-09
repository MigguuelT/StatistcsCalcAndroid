package com.miguel.statscalculator.core.util

import android.content.Context
import android.content.Intent

object ExportUtil {

    fun shareTextReport(context: Context, title: String, reportText: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, reportText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}