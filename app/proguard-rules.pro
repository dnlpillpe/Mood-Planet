# Mood Planet - reglas ProGuard/R8
# La app es 100% offline y no maneja datos sensibles fuera del dispositivo.

# Room
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Mantener modelos de datos (entidades Room) para evitar problemas de reflexión
-keep class com.kidslab.moodplanet.data.local.entity.** { *; }
