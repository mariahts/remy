# Reglas de ProGuard/R8 para la build de release.
#
# R8 no puede adivinar las clases que se usan por reflexion, por serializacion
# o por generacion de codigo. Aqui se declaran las de REMY y las de las librerias
# que dependen de la reflexion (Room, Supabase, Ktor, kotlinx.serialization, Coil).

# --- REMY -------------------------------------------------------------------
# Los datos de Room se guardan y se leen por nombre de columna y por tipo.
-keep class com.remy.app.data.** { *; }
-keep class com.remy.app.domain.model.** { *; }
# Los ViewModel se crean por reflexion desde los Composeables.
-keep class com.remy.app.ui.viewmodel.** { *; }

# --- Room -------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# --- kotlinx.serialization ---------------------------------------------------
# Los serializadores se generan como metodos estaticos y se localizan por nombre.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.remy.app.**$$serializer { *; }
-keepclassmembers class com.remy.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.remy.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Supabase / Ktor ---------------------------------------------------------
# El cliente de Supabase y el de Ktor registran y localizan clases por reflexion.
-keep class io.github.jan.tennert.supabase.** { *; }
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.tennert.supabase.**
-dontwarn io.ktor.**
-dontwarn org.slf4j.**
-keepclassmembers class io.ktor.** { volatile <fields>; }

# --- Coil --------------------------------------------------------------------
# Coil guarda las imágenes en cache con nombres de archivo generados.
-dontwarn coil.**

# --- AndroidX / Compose ------------------------------------------------------
# Los componentes de Compose con preview y los de Material 3 se localizan por nombre.
-dontwarn androidx.compose.**
-dontwarn androidx.camera.**
