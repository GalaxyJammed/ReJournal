package com.example.rejournal.data

object MotivationalMessages {

    private val veryTough = listOf(
        "The road to growth isn't easy. Take small steps each day.",
        "You don't have to have it all figured out today.",
        "This is hard right now, and that's valid. You're not alone.",
        "One day at a time — that's enough."
    )

    private val tough = listOf(
        "Rough patch lately — be gentle with yourself.",
        "Small steps count more than big leaps right now.",
        "It's okay to have hard days. You're still moving forward.",
        "Tough stretch — remember it's okay to reach out to someone."
    )

    private val neutral = listOf(
        "Steady days — a calm middle ground is okay too.",
        "Not every day needs to be a high. Balance counts.",
        "You're holding steady. That's its own kind of progress.",
        "An even keel lately — nothing wrong with that."
    )

    private val good = listOf(
        "Things are looking up — nice momentum!",
        "You're doing well. Keep building on this.",
        "Solid days lately. Keep the good vibes going.",
        "You're finding your rhythm — it shows."
    )

    private val great = listOf(
        "You're on a rejuvenating, healing path! Keep shining.",
        "Life feels good right now — savor it.",
        "You're riding a real high note. Enjoy this chapter.",
        "Radiant days like these are worth celebrating."
    )

    // moodLevel is a rounded average from 1 (roughest) to 5 (best).
    fun messagesFor(moodLevel: Int): List<String> = when (moodLevel) {
        1 -> veryTough
        2 -> tough
        3 -> neutral
        4 -> good
        else -> great
    }
}