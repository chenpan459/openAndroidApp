# 保留主 Activity
-keep class com.example.myapp.MainActivity { *; }

# 保留所有使用注解的类（如 Room、Gson）
-keepattributes *Annotation*

# 保留类名不被混淆
-keepnames class *

# 忽略警告（可选）
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# 日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
