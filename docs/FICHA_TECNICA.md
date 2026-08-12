# Ficha técnica — Mood Planet

| Campo | Valor |
|---|---|
| Nombre | Mood Planet |
| Repositorio | mood-planet-android |
| Package ID | com.kidslab.moodplanet |
| Versión | 1.0.0 |
| Idioma | Español |
| Audiencia | Niñas y niños de 8 a 12 años |
| Naturaleza | App educativa, **no terapéutica**, sin diagnóstico psicológico |
| Conectividad | 100% offline (sin permiso `INTERNET`) |

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM (Model-View-ViewModel) con un localizador de
  servicios simple (`ServiceLocator`) en lugar de un framework de
  inyección de dependencias, para mantener el proyecto ligero.
- **Persistencia:** Room (SQLite) — 100% local
- **Concurrencia:** Kotlin Coroutines + Flow / StateFlow
- **Navegación:** Navigation Compose
- **minSdk:** 24 (Android 7.0)
- **targetSdk / compileSdk:** 34
- **JDK:** 17 (con core library desugaring para `java.time` en API < 26)
- **Build:** Gradle (Kotlin DSL), Android Gradle Plugin

## Modelo de datos (Room)

10 entidades principales:

1. `UserProfile` — perfil local (nombre opcional, recordatorio diario)
2. `EmotionType` — catálogo fijo de 8 emociones
3. `EmotionEntry` — registros de "¿Cómo me siento?"
4. `TriggerCategory` — catálogo fijo de 6 categorías de "¿Qué pasó?"
5. `CopingTool` — catálogo fijo de 6 herramientas
6. `ToolSession` — registro de uso de cada herramienta
7. `EmotionalStory` — catálogo fijo de 15 historias emocionales
8. `StoryOption` — opciones de emoción/reacción de cada historia
9. `StoryAttempt` — intentos del usuario al resolver una historia
10. `Badge` / `UserBadge` — catálogo fijo de 7 insignias y su obtención

## Datos semilla (seed data)

- 8 emociones (alegría, tristeza, enojo, miedo, sorpresa, calma,
  vergüenza, preocupación)
- 6 categorías de disparador (escuela, familia, amigos, juego, cambios, otro)
- 6 herramientas (respiración, 5-4-3-2-1, estiramiento, pausa, mi voz
  tranquila, hablar con un adulto)
- 15 historias emocionales, cada una con 3 opciones de emoción y 3 de
  reacción
- 7 insignias

## Micrófono

El ejercicio opcional "Mi voz tranquila" usa `AudioRecord` (PCM 16 bits,
16 kHz, mono) exclusivamente para calcular un nivel de volumen aproximado
en memoria. No hay reconocimiento de voz, no se persiste audio en disco y
el permiso `RECORD_AUDIO` se solicita solo al abrir el ejercicio. Existe
una alternativa sin micrófono y la denegación del permiso no bloquea la
aplicación.

## Pantallas (9)

1. Bienvenida / mascota
2. ¿Cómo me siento? (8 emociones, intensidad 1-3)
3. ¿Qué pasó? (6 categorías + nota opcional de 80 caracteres)
4. Caja de herramientas
5. Respiración (animación 4-2-4, 3 rondas, temporizador)
6. Historias emocionales (15 historias)
7. Planeta semanal (resumen de los últimos 7 días)
8. Colección de herramientas
9. Logros / Ajustes (7 insignias)

## Pruebas

Pruebas unitarias (JVM, Robolectric + Room en memoria) para: registro
emocional, validación de intensidad, resumen semanal, uso de herramientas,
respiración (temporizador de 3 rondas), historias (15 historias con 3+3
opciones), insignias (otorgamiento e idempotencia), persistencia
(cierre/reapertura de base de datos) y denegación del permiso de
micrófono (no bloquea la app). Prueba instrumentada de humo para el
arranque de la app.

## CI/CD

- `android-build.yml`: compila, ejecuta las pruebas unitarias y genera el
  APK de la app (`MoodPlanet-v1.0.0.apk`) en cada push/PR y en el tag de
  versión.
- `docs-build.yml`: genera los 3 PDFs de documentación (guía para
  familias/docentes, ficha técnica y política de privacidad) a partir de
  los archivos Markdown en `docs/`.
