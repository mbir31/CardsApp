# Add project specific ProGuard rules here.

# Keep Room database entities & DAO implementations
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep data models
-keep class com.example.model.** { *; }

# ML Kit Text Recognition
-keep class com.google.mlkit.vision.** { *; }
-dontwarn com.google.mlkit.**

# Firebase AI / Gemini
-dontwarn com.google.firebase.**

# Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
