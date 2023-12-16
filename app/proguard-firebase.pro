# Begin: Proguard rules for Firebase

# Authentication
-keepattributes *Annotation*

# Add this global rule
-keepattributes Signature

-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep class com.google.android.gms.internal.consent_sdk.** { *; }

# End: Proguard rules for Firebase