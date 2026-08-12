package com.kidslab.moodplanet.navigation

/** Rutas de navegación de las 9 pantallas de Mood Planet. */
object Routes {
    const val WELCOME = "welcome"
    const val HOW_DO_I_FEEL = "how_do_i_feel"
    const val WHAT_HAPPENED = "what_happened/{emotionTypeId}/{intensity}"
    const val TOOLBOX = "toolbox?entryId={entryId}"
    const val BREATHING = "breathing?entryId={entryId}"
    const val STORIES = "stories"
    const val STORY_DETAIL = "story_detail/{storyId}"
    const val WEEKLY_PLANET = "weekly_planet"
    const val TOOL_COLLECTION = "tool_collection"
    const val ACHIEVEMENTS_SETTINGS = "achievements_settings"
    const val CALM_VOICE = "calm_voice?entryId={entryId}"
    const val TALK_TO_ADULT = "talk_to_adult"

    fun whatHappened(emotionTypeId: Int, intensity: Int) = "what_happened/$emotionTypeId/$intensity"
    fun toolbox(entryId: Long? = null) = "toolbox?entryId=${entryId ?: -1}"
    fun breathing(entryId: Long? = null) = "breathing?entryId=${entryId ?: -1}"
    fun storyDetail(storyId: Int) = "story_detail/$storyId"
    fun calmVoice(entryId: Long? = null) = "calm_voice?entryId=${entryId ?: -1}"
}
