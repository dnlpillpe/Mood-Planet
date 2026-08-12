package com.kidslab.moodplanet.data.local.seed

import com.kidslab.moodplanet.data.local.entity.Badge
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.local.entity.EmotionType
import com.kidslab.moodplanet.data.local.entity.EmotionalStory
import com.kidslab.moodplanet.data.local.entity.StoryOption
import com.kidslab.moodplanet.data.local.entity.TriggerCategory

/**
 * Datos semilla fijos de Mood Planet: 8 emociones, 6 categorías de
 * disparador, 6 herramientas, 15 historias (con 3 opciones de emoción y
 * 3 opciones de reacción cada una) y 7 insignias.
 *
 * Todo el contenido es educativo, en español, para niños de 8 a 12 años.
 * No incluye lenguaje clínico ni etiquetas de diagnóstico.
 */
object SeedData {

    // ---------------------------------------------------------------
    // Emociones (ids estables: se usan como claves foráneas fijas)
    // ---------------------------------------------------------------
    object EmotionIds {
        const val ALEGRIA = 1
        const val TRISTEZA = 2
        const val ENOJO = 3
        const val MIEDO = 4
        const val SORPRESA = 5
        const val CALMA = 6
        const val VERGUENZA = 7
        const val PREOCUPACION = 8
    }

    val emotionTypes = listOf(
        EmotionType(EmotionIds.ALEGRIA, "Alegría", "😄", "#FFD166", 1),
        EmotionType(EmotionIds.TRISTEZA, "Tristeza", "😢", "#5B8DEF", 2),
        EmotionType(EmotionIds.ENOJO, "Enojo", "😠", "#EF5B5B", 3),
        EmotionType(EmotionIds.MIEDO, "Miedo", "😨", "#7B61FF", 4),
        EmotionType(EmotionIds.SORPRESA, "Sorpresa", "😮", "#FF8FB1", 5),
        EmotionType(EmotionIds.CALMA, "Calma", "😌", "#4ECDC4", 6),
        EmotionType(EmotionIds.VERGUENZA, "Vergüenza", "😳", "#F4A259", 7),
        EmotionType(EmotionIds.PREOCUPACION, "Preocupación", "😟", "#8D8DAA", 8)
    )

    // ---------------------------------------------------------------
    // Categorías de disparador
    // ---------------------------------------------------------------
    object TriggerIds {
        const val ESCUELA = 1
        const val FAMILIA = 2
        const val AMIGOS = 3
        const val JUEGO = 4
        const val CAMBIOS = 5
        const val OTRO = 6
    }

    val triggerCategories = listOf(
        TriggerCategory(TriggerIds.ESCUELA, "Escuela", "🏫", 1),
        TriggerCategory(TriggerIds.FAMILIA, "Familia", "🏠", 2),
        TriggerCategory(TriggerIds.AMIGOS, "Amigos", "🧑‍🤝‍🧑", 3),
        TriggerCategory(TriggerIds.JUEGO, "Juego", "🎮", 4),
        TriggerCategory(TriggerIds.CAMBIOS, "Cambios", "🔄", 5),
        TriggerCategory(TriggerIds.OTRO, "Otro", "✨", 6)
    )

    // ---------------------------------------------------------------
    // Herramientas de autorregulación
    // ---------------------------------------------------------------
    object ToolIds {
        const val BREATHING = 1
        const val GROUNDING_54321 = 2
        const val STRETCH = 3
        const val PAUSE = 4
        const val CALM_VOICE = 5
        const val TALK_ADULT = 6
    }

    val copingTools = listOf(
        CopingTool(
            ToolIds.BREATHING, "breathing", "Respiración",
            "Respira lento en 3 rondas para sentirte más tranquilo.", "air", 1
        ),
        CopingTool(
            ToolIds.GROUNDING_54321, "grounding_54321", "5-4-3-2-1",
            "Nota cosas a tu alrededor para volver al presente.", "visibility", 2
        ),
        CopingTool(
            ToolIds.STRETCH, "stretch", "Estiramiento",
            "Mueve tu cuerpo suavemente para liberar tensión.", "self_improvement", 3
        ),
        CopingTool(
            ToolIds.PAUSE, "pause", "Pausa",
            "Tómate un momento de silencio antes de continuar.", "pause_circle", 4
        ),
        CopingTool(
            ToolIds.CALM_VOICE, "calm_voice", "Mi voz tranquila",
            "Practica hablar despacio y con un volumen moderado.", "mic", 5
        ),
        CopingTool(
            ToolIds.TALK_ADULT, "talk_adult", "Hablar con un adulto",
            "A veces ayuda mucho contarle a alguien de confianza.", "diversity_3", 6
        )
    )

    // ---------------------------------------------------------------
    // Insignias
    // ---------------------------------------------------------------
    object BadgeIds {
        const val FIRST_ENTRY = 1
        const val EXPLORER = 2
        const val CONSISTENT_WEEK = 3
        const val BREATHING_MASTER = 4
        const val FULL_TOOLBOX = 5
        const val STORYTELLER = 6
        const val CALM_VOICE_PRO = 7
    }

    val badges = listOf(
        Badge(
            BadgeIds.FIRST_ENTRY, "first_entry", "Primer paso",
            "Registraste cómo te sentías por primera vez.", "flag", 1
        ),
        Badge(
            BadgeIds.EXPLORER, "explorer", "Explorador de emociones",
            "Registraste 5 emociones distintas veces.", "explore", 2
        ),
        Badge(
            BadgeIds.CONSISTENT_WEEK, "consistent_week", "Semana constante",
            "Registraste tus emociones en 7 días diferentes.", "calendar_month", 3
        ),
        Badge(
            BadgeIds.BREATHING_MASTER, "breathing_master", "Maestro de la respiración",
            "Practicaste el ejercicio de respiración 5 veces.", "air", 4
        ),
        Badge(
            BadgeIds.FULL_TOOLBOX, "full_toolbox", "Caja de herramientas completa",
            "Probaste las 6 herramientas al menos una vez.", "toolbox", 5
        ),
        Badge(
            BadgeIds.STORYTELLER, "storyteller", "Cuentacuentos emocional",
            "Completaste las 15 historias emocionales.", "auto_stories", 6
        ),
        Badge(
            BadgeIds.CALM_VOICE_PRO, "calm_voice_pro", "Voz tranquila",
            "Practicaste tu voz tranquila 3 veces.", "record_voice_over", 7
        )
    )

    // ---------------------------------------------------------------
    // Historias emocionales (15) con opciones de emoción y reacción
    // ---------------------------------------------------------------
    private const val EMOTION = "EMOTION"
    private const val REACTION = "REACTION"

    private class StoryBuilder(
        val id: Int,
        val title: String,
        val scenario: String,
        val targetEmotionId: Int,
        val emotionOptionIds: List<Int>,
        val reactionTexts: List<Pair<String, Boolean>>
    )

    private val storyBuilders = listOf(
        StoryBuilder(
            1, "El examen sorpresa",
            "Ana llega al salón y la maestra anuncia un examen que nadie esperaba. A Ana le tiemblan un poco las manos.",
            EmotionIds.MIEDO,
            listOf(EmotionIds.MIEDO, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Respirar hondo y leer las preguntas con calma" to true,
                "Levantarse y salir del salón sin avisar" to false,
                "Quedarse mirando la hoja sin hacer nada" to false
            )
        ),
        StoryBuilder(
            2, "El juguete roto",
            "Leo estaba armando su juguete favorito y, sin querer, se le cayó y se rompió una pieza.",
            EmotionIds.TRISTEZA,
            listOf(EmotionIds.TRISTEZA, EmotionIds.SORPRESA, EmotionIds.CALMA),
            listOf(
                "Contarle a un adulto lo que pasó y pedir ayuda" to true,
                "Esconder el juguete roto y no decir nada" to false,
                "Tirar otros juguetes por el suelo" to false
            )
        ),
        StoryBuilder(
            3, "El partido perdido",
            "El equipo de Mateo perdió el partido de fútbol en el último minuto. Mateo aprieta los puños.",
            EmotionIds.ENOJO,
            listOf(EmotionIds.ENOJO, EmotionIds.ALEGRIA, EmotionIds.VERGUENZA),
            listOf(
                "Hacer una pausa y respirar antes de hablar" to true,
                "Gritarle a un compañero de equipo" to false,
                "Patear la mochila con fuerza" to false
            )
        ),
        StoryBuilder(
            4, "La mudanza",
            "La familia de Sofía se va a mudar a otra ciudad la próxima semana. Sofía piensa mucho en eso antes de dormir.",
            EmotionIds.PREOCUPACION,
            listOf(EmotionIds.PREOCUPACION, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Hablar con un adulto sobre cómo se siente" to true,
                "Guardarse todo y no contarle a nadie" to false,
                "Fingir que no le importa" to false
            )
        ),
        StoryBuilder(
            5, "La fiesta sorpresa",
            "Cuando Diego abre la puerta de su casa, todos sus amigos gritan '¡Sorpresa!' para su cumpleaños.",
            EmotionIds.SORPRESA,
            listOf(EmotionIds.SORPRESA, EmotionIds.MIEDO, EmotionIds.TRISTEZA),
            listOf(
                "Sonreír y disfrutar el momento con sus amigos" to true,
                "Salir corriendo asustado" to false,
                "Ignorar a todos y irse a su cuarto" to false
            )
        ),
        StoryBuilder(
            6, "El nuevo compañero",
            "Llega un compañero nuevo al salón y nadie lo conoce todavía. Camila nota que él está solo en el recreo.",
            EmotionIds.PREOCUPACION,
            listOf(EmotionIds.PREOCUPACION, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Acercarse y saludarlo con amabilidad" to true,
                "Señalarlo y reírse con sus amigos" to false,
                "No hacer nada y seguir jugando" to false
            )
        ),
        StoryBuilder(
            7, "El regaño injusto",
            "Marco siente que lo regañaron por algo que no hizo. Se le pone la cara roja y siente ganas de gritar.",
            EmotionIds.ENOJO,
            listOf(EmotionIds.ENOJO, EmotionIds.CALMA, EmotionIds.SORPRESA),
            listOf(
                "Contar hasta diez y explicar con calma lo que pasó" to true,
                "Contestar de mala manera" to false,
                "Aventar sus cosas al suelo" to false
            )
        ),
        StoryBuilder(
            8, "Se me olvidó la tarea",
            "Valentina se da cuenta en el salón de que dejó su tarea en casa. Siente un nudo en el estómago.",
            EmotionIds.PREOCUPACION,
            listOf(EmotionIds.PREOCUPACION, EmotionIds.ALEGRIA, EmotionIds.CALMA),
            listOf(
                "Avisarle a la maestra con honestidad" to true,
                "Copiarle la tarea a un compañero a escondidas" to false,
                "Quedarse callada esperando que nadie note" to false
            )
        ),
        StoryBuilder(
            9, "Me tropecé frente a todos",
            "Iván se tropezó en el pasillo y varios compañeros lo vieron. Siente que se le pone la cara caliente.",
            EmotionIds.VERGUENZA,
            listOf(EmotionIds.VERGUENZA, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Reírse un poco de la situación y seguir caminando" to true,
                "Esconderse en el baño todo el recreo" to false,
                "Empujar a quien se rió" to false
            )
        ),
        StoryBuilder(
            10, "El dibujo premiado",
            "La maestra elige el dibujo de Renata para ponerlo en la pared del salón frente a toda la clase.",
            EmotionIds.ALEGRIA,
            listOf(EmotionIds.ALEGRIA, EmotionIds.TRISTEZA, EmotionIds.MIEDO),
            listOf(
                "Agradecer y compartir la alegría con sus amigos" to true,
                "Presumir de forma que hiere a los demás" to false,
                "Quitarle importancia y esconder el dibujo" to false
            )
        ),
        StoryBuilder(
            11, "Perro perdido en el parque",
            "En el parque, Bruno ve a un perrito solo y asustado, sin su dueño cerca.",
            EmotionIds.PREOCUPACION,
            listOf(EmotionIds.PREOCUPACION, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Avisarle a un adulto de confianza para ayudar al perrito" to true,
                "Perseguir al perrito solo, lejos de los adultos" to false,
                "No hacer nada y seguir jugando" to false
            )
        ),
        StoryBuilder(
            12, "La tormenta de noche",
            "Truena muy fuerte afuera mientras Camila intenta dormir. Se tapa con la cobija hasta la cabeza.",
            EmotionIds.MIEDO,
            listOf(EmotionIds.MIEDO, EmotionIds.ALEGRIA, EmotionIds.SORPRESA),
            listOf(
                "Respirar despacio y buscar a un adulto si lo necesita" to true,
                "Quedarse muy quieta sin decir nada a nadie" to false,
                "Gritar y llorar sin parar" to false
            )
        ),
        StoryBuilder(
            13, "Cambio de maestra",
            "Anuncian que la maestra de todo el año se va y llegará una nueva después de las vacaciones.",
            EmotionIds.PREOCUPACION,
            listOf(EmotionIds.PREOCUPACION, EmotionIds.ALEGRIA, EmotionIds.ENOJO),
            listOf(
                "Hablar de sus dudas con un adulto de confianza" to true,
                "Decidir portarse mal con la maestra nueva" to false,
                "Guardarse la preocupación y no dormir bien" to false
            )
        ),
        StoryBuilder(
            14, "Perdí en el juego de mesa",
            "Tomás pierde en el juego de mesa contra su hermana por segunda vez seguida.",
            EmotionIds.ENOJO,
            listOf(EmotionIds.ENOJO, EmotionIds.CALMA, EmotionIds.ALEGRIA),
            listOf(
                "Felicitar a su hermana aunque le cueste un poco" to true,
                "Voltear el tablero del juego" to false,
                "Dejar de hablarle a su hermana el resto del día" to false
            )
        ),
        StoryBuilder(
            15, "Un logro después de practicar mucho",
            "Después de practicar todas las semanas, Marina por fin logra nadar todo el largo de la piscina.",
            EmotionIds.ALEGRIA,
            listOf(EmotionIds.ALEGRIA, EmotionIds.MIEDO, EmotionIds.VERGUENZA),
            listOf(
                "Celebrar su esfuerzo y contárselo a su familia" to true,
                "Pensar que fue solo suerte y no decir nada" to false,
                "Burlarse de los compañeros que no lo lograron" to false
            )
        )
    )

    val emotionalStories: List<EmotionalStory> = storyBuilders.map { b ->
        EmotionalStory(
            id = b.id,
            title = b.title,
            scenarioText = b.scenario,
            targetEmotionTypeId = b.targetEmotionId,
            orderIndex = b.id
        )
    }

    val storyOptions: List<StoryOption> = storyBuilders.flatMap { b ->
        val emotionNamesById = emotionTypes.associateBy { it.id }
        val emotionOpts = b.emotionOptionIds.mapIndexed { index, emoId ->
            StoryOption(
                storyId = b.id,
                stepType = EMOTION,
                text = emotionNamesById.getValue(emoId).name,
                isRecommended = emoId == b.targetEmotionId,
                orderIndex = index
            )
        }
        val reactionOpts = b.reactionTexts.mapIndexed { index, (text, recommended) ->
            StoryOption(
                storyId = b.id,
                stepType = REACTION,
                text = text,
                isRecommended = recommended,
                orderIndex = index
            )
        }
        emotionOpts + reactionOpts
    }
}
