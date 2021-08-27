# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontobfuscate

#
# KotlinX Serialization
#

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class dev.librecybernetics.coopcycle.**$$serializer { *; }
-keepclassmembers class dev.librecybernetics.coopcycle.** {
    *** Companion;
}
-keepclasseswithmembers class dev.librecybernetics.coopcycle.** {
    kotlinx.serialization.KSerializer serializer(...);
}