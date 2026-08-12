# Mood Planet 🪐

Mood Planet es una aplicación Android **educativa**, en español, para que
niñas y niños de 8 a 12 años reconozcan sus emociones y practiquen
estrategias sencillas de autorregulación.

> **Mood Planet no es terapia, no realiza diagnósticos psicológicos y no
> interpreta sus registros como una enfermedad.** En situaciones difíciles,
> la app siempre sugiere hablar con una persona adulta de confianza. Ver
> [`docs/PRIVACIDAD.md`](docs/PRIVACIDAD.md) y
> [`docs/GUIA_PADRES_DOCENTES.md`](docs/GUIA_PADRES_DOCENTES.md).

| | |
|---|---|
| Package ID | `com.kidslab.moodplanet` |
| Versión | 1.0.0 |
| minSdk | 24 (Android 7.0) |
| Idioma | Español |
| Conectividad | 100% offline |

## Pantallas

1. Bienvenida / mascota (Nova)
2. ¿Cómo me siento? — 8 emociones, intensidad 1-3
3. ¿Qué pasó? — 6 categorías + nota opcional (80 caracteres)
4. Caja de herramientas — 6 herramientas
5. Respiración — animación 4-2-4, 3 rondas, temporizador
6. Historias emocionales — 15 historias interactivas
7. Mi planeta semanal — resumen de los últimos 7 días
8. Colección de herramientas
9. Logros / Ajustes — 7 insignias

## Tecnología

Kotlin · Jetpack Compose · Material 3 · MVVM · Room · Coroutines · Navigation
Compose · 100% offline · minSdk 24 · JDK 17.

Ver el detalle completo en [`docs/FICHA_TECNICA.md`](docs/FICHA_TECNICA.md).

## Micrófono: "Mi voz tranquila"

Ejercicio opcional donde el niño o niña dice una frase corta y ve un nivel
aproximado de volumen (sin reconocimiento de voz, sin grabar ni subir
audio). El permiso `RECORD_AUDIO` se pide solo al abrir el ejercicio y
siempre existe una alternativa sin micrófono; negar el permiso nunca
bloquea la app. Detalle técnico y de privacidad en
[`docs/PRIVACIDAD.md`](docs/PRIVACIDAD.md).

## Compilar el proyecto

Requisitos: JDK 17, Android SDK (compileSdk 34), conexión a internet para
resolver dependencias de Gradle/Maven la primera vez.

```bash
./gradlew assembleDebug      # APK de depuración
./gradlew testDebugUnitTest  # pruebas unitarias (JVM/Robolectric)
./gradlew connectedAndroidTest  # pruebas instrumentadas (requiere emulador/dispositivo)
./gradlew assembleRelease    # APK de release (firmado si se configuran las claves)
```

## Estructura del repositorio

```
mood-planet-android/
├── app/                        # Módulo de la app Android
│   └── src/
│       ├── main/                # Código de producción (Kotlin + Compose)
│       ├── test/                # Pruebas unitarias (JVM, Robolectric)
│       └── androidTest/         # Pruebas instrumentadas
├── docs/                       # Documentación + fuentes de los 3 PDF
├── .github/workflows/          # CI: build/test/release y generación de PDFs
└── build.gradle.kts, settings.gradle.kts, gradlew, ...
```

## Pruebas

El proyecto incluye pruebas unitarias para: registro de emociones,
validación de intensidad, resumen semanal, uso de herramientas,
respiración (3 rondas con temporizador), historias emocionales (15
historias), insignias (otorgamiento e idempotencia), persistencia de datos
y denegación del permiso de micrófono sin bloquear la app. Además incluye
una prueba instrumentada de humo.

## CI/CD

- **`android-build.yml`** — build, lint y pruebas unitarias en cada push y
  pull request; genera `MoodPlanet-v1.0.0.apk` y crea un release de GitHub
  cuando se publica un tag `v*`.
- **`docs-build.yml`** — genera los 3 PDFs de documentación a partir de los
  Markdown en `docs/` y los publica como artefactos del workflow.

## Licencia y alcance

Mood Planet es un proyecto educativo. No sustituye la orientación de
profesionales de salud mental, pediatría o educación.
