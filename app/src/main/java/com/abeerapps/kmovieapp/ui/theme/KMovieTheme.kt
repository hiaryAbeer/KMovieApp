package com.abeerapps.kmovieapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.abeerapps.kmovieapp.R

private val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

private val KMovieTypography = Typography().run {
    fun TextStyle.poppins() = copy(fontFamily = Poppins)
    copy(
        displayLarge = displayLarge.poppins(),
        displayMedium = displayMedium.poppins(),
        displaySmall = displaySmall.poppins(),
        headlineLarge = headlineLarge.poppins(),
        headlineMedium = headlineMedium.poppins(),
        headlineSmall = headlineSmall.poppins(),
        titleLarge = titleLarge.poppins(),
        titleMedium = titleMedium.poppins(),
        titleSmall = titleSmall.poppins(),
        bodyLarge = bodyLarge.poppins(),
        bodyMedium = bodyMedium.poppins(),
        bodySmall = bodySmall.poppins(),
        labelLarge = labelLarge.poppins(),
        labelMedium = labelMedium.poppins(),
        labelSmall = labelSmall.poppins(),
    )
}

/**
 * Compose theme for screens migrated from XML. Colors come from the same color resources
 * as the XML theme (values / values-night), so both stay in sync, including the
 * Light / Dark / System choice made in the drawer.
 */
@Composable
fun KMovieTheme(content: @Composable () -> Unit) {
    val base = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    val colorScheme = base.copy(
        primary = colorResource(R.color.brand_yellow),
        onPrimary = colorResource(R.color.on_brand),
        primaryContainer = colorResource(R.color.brand_yellow_light),
        onPrimaryContainer = colorResource(R.color.on_brand),
        background = colorResource(R.color.surface),
        onBackground = colorResource(R.color.text_primary),
        surface = colorResource(R.color.surface),
        onSurface = colorResource(R.color.text_primary),
        onSurfaceVariant = colorResource(R.color.text_secondary),
        outline = colorResource(R.color.text_secondary),
        error = colorResource(R.color.favorite_red),
    )
    MaterialTheme(colorScheme = colorScheme, typography = KMovieTypography, content = content)
}
