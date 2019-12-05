# Begin: Common Proguard rules

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

# Add when compile with JDK 1.7
-keepattributes EnclosingMethod

-keep class android.support.v7.widget.SearchView { *; }
-keepclassmembers class com.appskimo.app.japanese.domain.** { *; }

-dontwarn com.caverock.androidsvg.**

# End: Common Proguard rules
