package com.feniworks.countrycodenew

import android.content.Context
import android.graphics.drawable.Drawable
import java.util.*

class CountriesListItem(
    val id: String,
    val title: String,
    prefix: String,
    timezone: String?,
    val iso2: String,
    context: Context
) {
    val prefix: String
    val timezone: String
    var icon: Drawable? = null
        private set

    init {
        val tz = timezone ?: "N/A"
        this.prefix = "+$prefix"
        this.timezone = "UTC $tz"

        val imgID = context.resources.getIdentifier(
            "flag_${iso2.lowercase(Locale.ROOT)}",
            "drawable",
            context.packageName
        )
        if (imgID != 0)
            this.icon = context.resources.getDrawable(imgID)
    }
}
