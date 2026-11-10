package com.remy.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.remy.app.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val nunitoFont = GoogleFont("Nunito")

private val RemyFontFamily = FontFamily(
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Bold),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp, lineHeight = 40.sp),
    displayMedium = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp),
    displaySmall = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineLarge = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    headlineMedium = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 14.sp),
)
