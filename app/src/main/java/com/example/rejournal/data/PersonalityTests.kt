package com.example.rejournal.data

enum class MbtiAxis(val posLetter: Char, val negLetter: Char) {
    EI('E', 'I'),
    SN('N', 'S'),
    TF('F', 'T'),
    JP('P', 'J')
}

data class MbtiQuestion(val id: Int, val text: String, val axis: MbtiAxis, val polarity: Int)

data class MbtiResult(val type: String, val nickname: String, val description: String)

object MbtiTest {
    val questions: List<MbtiQuestion> = listOf(
        MbtiQuestion(1, "I feel energized after spending time with a large group of people.", MbtiAxis.EI, 1),
        MbtiQuestion(2, "I often think out loud when working through a problem.", MbtiAxis.EI, 1),
        MbtiQuestion(3, "I enjoy being the center of attention at social gatherings.", MbtiAxis.EI, 1),
        MbtiQuestion(4, "I find it easy to strike up conversations with strangers.", MbtiAxis.EI, 1),
        MbtiQuestion(5, "I prefer working in a lively, bustling environment.", MbtiAxis.EI, 1),
        MbtiQuestion(6, "I need quiet alone time to recharge after socializing.", MbtiAxis.EI, -1),
        MbtiQuestion(7, "I prefer reflecting privately before sharing my thoughts.", MbtiAxis.EI, -1),
        MbtiQuestion(8, "Small, intimate gatherings appeal to me more than big parties.", MbtiAxis.EI, -1),
        MbtiQuestion(9, "I often feel drained after long periods of socializing.", MbtiAxis.EI, -1),
        MbtiQuestion(10, "I'd rather listen than speak in group discussions.", MbtiAxis.EI, -1),

        MbtiQuestion(11, "I'm more interested in future possibilities than present realities.", MbtiAxis.SN, 1),
        MbtiQuestion(12, "I enjoy exploring abstract theories and ideas.", MbtiAxis.SN, 1),
        MbtiQuestion(13, "I often notice patterns and connections others miss.", MbtiAxis.SN, 1),
        MbtiQuestion(14, "I trust my gut instincts over concrete evidence.", MbtiAxis.SN, 1),
        MbtiQuestion(15, "I get bored with routine, step-by-step tasks.", MbtiAxis.SN, 1),
        MbtiQuestion(16, "I focus on practical, real-world details.", MbtiAxis.SN, -1),
        MbtiQuestion(17, "I prefer clear, factual instructions over abstract concepts.", MbtiAxis.SN, -1),
        MbtiQuestion(18, "I trust direct experience more than theoretical speculation.", MbtiAxis.SN, -1),
        MbtiQuestion(19, "I like tasks with clear, concrete steps.", MbtiAxis.SN, -1),
        MbtiQuestion(20, "I pay close attention to specifics and details.", MbtiAxis.SN, -1),

        MbtiQuestion(21, "I make decisions based on how they'll affect people's feelings.", MbtiAxis.TF, 1),
        MbtiQuestion(22, "Harmony in relationships matters more to me than being right.", MbtiAxis.TF, 1),
        MbtiQuestion(23, "I find it easy to empathize with others' emotions.", MbtiAxis.TF, 1),
        MbtiQuestion(24, "I consider personal values before logic when deciding.", MbtiAxis.TF, 1),
        MbtiQuestion(25, "I go out of my way to avoid hurting others' feelings.", MbtiAxis.TF, 1),
        MbtiQuestion(26, "I prioritize logic and fairness over personal feelings.", MbtiAxis.TF, -1),
        MbtiQuestion(27, "I can make tough decisions without being swayed by emotion.", MbtiAxis.TF, -1),
        MbtiQuestion(28, "I value truth even if it might upset someone.", MbtiAxis.TF, -1),
        MbtiQuestion(29, "I analyze problems objectively before considering feelings.", MbtiAxis.TF, -1),
        MbtiQuestion(30, "Being right matters more to me than being liked.", MbtiAxis.TF, -1),

        MbtiQuestion(31, "I prefer to keep my options open rather than settle plans early.", MbtiAxis.JP, 1),
        MbtiQuestion(32, "I work best under the pressure of a last-minute deadline.", MbtiAxis.JP, 1),
        MbtiQuestion(33, "I enjoy spontaneity more than following a strict schedule.", MbtiAxis.JP, 1),
        MbtiQuestion(34, "My living or working space tends to be relaxed rather than orderly.", MbtiAxis.JP, 1),
        MbtiQuestion(35, "I adapt easily when plans suddenly change.", MbtiAxis.JP, 1),
        MbtiQuestion(36, "I like having a clear plan and sticking to it.", MbtiAxis.JP, -1),
        MbtiQuestion(37, "I feel uneasy leaving things unfinished or undecided.", MbtiAxis.JP, -1),
        MbtiQuestion(38, "I prefer to complete tasks well before the deadline.", MbtiAxis.JP, -1),
        MbtiQuestion(39, "I like organizing my space and schedule in advance.", MbtiAxis.JP, -1),
        MbtiQuestion(40, "I feel more comfortable when decisions are made early.", MbtiAxis.JP, -1)
    )

    private val typeDescriptions: Map<String, MbtiResult> = mapOf(
        "INTJ" to MbtiResult("INTJ", "The Strategist", "Independent, analytical, and driven by long-term vision. You value competence and tend to plan several steps ahead."),
        "INTP" to MbtiResult("INTP", "The Thinker", "Curious and logical, you love exploring ideas for their own sake and questioning how things really work."),
        "ENTJ" to MbtiResult("ENTJ", "The Commander", "Decisive and confident, you naturally take charge and push toward ambitious goals."),
        "ENTP" to MbtiResult("ENTP", "The Debater", "Quick-witted and inventive, you enjoy challenging ideas and exploring new possibilities out loud."),
        "INFJ" to MbtiResult("INFJ", "The Advocate", "Insightful and idealistic, you're driven by a quiet sense of purpose and deep concern for others."),
        "INFP" to MbtiResult("INFP", "The Mediator", "Empathetic and imaginative, you're guided by strong personal values and a rich inner world."),
        "ENFJ" to MbtiResult("ENFJ", "The Protagonist", "Warm and persuasive, you naturally inspire and organize people around a shared cause."),
        "ENFP" to MbtiResult("ENFP", "The Campaigner", "Enthusiastic and imaginative, you thrive on new connections and creative possibilities."),
        "ISTJ" to MbtiResult("ISTJ", "The Logistician", "Practical and dependable, you value order, tradition, and following through on commitments."),
        "ISFJ" to MbtiResult("ISFJ", "The Defender", "Warm and conscientious, you quietly take care of the people and responsibilities around you."),
        "ESTJ" to MbtiResult("ESTJ", "The Executive", "Organized and assertive, you like clear structure and getting things done efficiently."),
        "ESFJ" to MbtiResult("ESFJ", "The Consul", "Sociable and caring, you thrive on harmony and supporting the people close to you."),
        "ISTP" to MbtiResult("ISTP", "The Virtuoso", "Practical and adaptable, you enjoy hands-on problem solving and figuring out how things work."),
        "ISFP" to MbtiResult("ISFP", "The Adventurer", "Gentle and spontaneous, you value personal freedom and quiet self-expression."),
        "ESTP" to MbtiResult("ESTP", "The Entrepreneur", "Energetic and bold, you live in the moment and enjoy taking calculated risks."),
        "ESFP" to MbtiResult("ESFP", "The Entertainer", "Playful and warm, you bring energy to any room and enjoy living spontaneously.")
    )

    fun score(answers: Map<Int, Int>): MbtiResult {
        val axisTotals = mutableMapOf<MbtiAxis, Int>()
        questions.forEach { q ->
            val value = answers[q.id] ?: 3
            axisTotals[q.axis] = (axisTotals[q.axis] ?: 0) + q.polarity * (value - 3)
        }
        val type = MbtiAxis.entries.joinToString("") { axis ->
            val total = axisTotals[axis] ?: 0
            (if (total >= 0) axis.posLetter else axis.negLetter).toString()
        }
        return typeDescriptions[type] ?: MbtiResult(type, "Your Type", "A unique combination of traits.")
    }
}

data class NpiQuestion(val id: Int, val optionA: String, val optionB: String)

object NpiTest {
    val questions: List<NpiQuestion> = listOf(
        NpiQuestion(1, "I don't mind following others' lead.", "I see myself as a natural leader."),
        NpiQuestion(2, "Being average is fine with me.", "I know I'm meant for something extraordinary."),
        NpiQuestion(3, "I'm no more capable than anyone else.", "I have a knack for doing things better than most people."),
        NpiQuestion(4, "I try not to show off.", "I like showing off from time to time."),
        NpiQuestion(5, "Compliments don't matter much to me.", "I really enjoy being complimented."),
        NpiQuestion(6, "I'm not particularly interested in how I look.", "I like to look at my reflection."),
        NpiQuestion(7, "The world doesn't owe me anything.", "I honestly feel I deserve special treatment."),
        NpiQuestion(8, "Ordinary responsibilities are fine with me.", "I'd hate a job where I'm not in charge."),
        NpiQuestion(9, "I'm not that concerned with being admired.", "I want to amount to something in others' eyes."),
        NpiQuestion(10, "I depend on others quite a bit.", "I prefer relying only on myself."),
        NpiQuestion(11, "I don't often influence people around me.", "I can usually talk my way into getting what I want."),
        NpiQuestion(12, "I'm one of many capable people.", "I honestly believe I'm a cut above most people."),
        NpiQuestion(13, "Leadership isn't something I crave.", "I'd love to be seen as a leader."),
        NpiQuestion(14, "I have no special talent for persuading people.", "I'm good at getting people to believe what I say."),
        NpiQuestion(15, "I'm content blending into the crowd.", "I like being the center of attention."),
        NpiQuestion(16, "I take life as it comes without expecting much.", "I expect a lot from life because I deserve it.")
    )

    fun score(answers: Map<Int, Boolean>): Int = answers.values.count { it }

    fun interpretation(score: Int): String = when {
        score <= 4 -> "Your responses lean toward modesty and low self-focus - well below where most people tend to score."
        score <= 8 -> "Your responses land in the range most people fall into - a typical, moderate level of self-focus."
        score <= 12 -> "Your responses lean toward elevated confidence and self-focus, higher than most people."
        else -> "Your responses lean strongly toward grandiosity and self-focus - notably higher than most people."
    }
}

enum class DarkTriadTrait(val displayName: String) {
    MACHIAVELLIANISM("Machiavellianism"),
    GRANDIOSITY("Grandiosity"),
    PSYCHOPATHY("Psychopathy")
}

data class DarkTriadQuestion(val id: Int, val text: String, val trait: DarkTriadTrait)

object DarkTriadTest {
    val questions: List<DarkTriadQuestion> = listOf(
        DarkTriadQuestion(1, "I think it's wise to keep some plans secret from others.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(2, "Most people can be manipulated if you know how.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(3, "I avoid telling people my real intentions.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(4, "It's smart to flatter people to get what you want.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(5, "I believe morality is often just a matter of convenience.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(6, "I'm willing to bend the truth if it benefits me.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(7, "I prefer to have a hidden advantage over others.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(8, "Trusting people fully is a mistake.", DarkTriadTrait.MACHIAVELLIANISM),
        DarkTriadQuestion(9, "I plan several steps ahead to get what I want from others.", DarkTriadTrait.MACHIAVELLIANISM),

        DarkTriadQuestion(10, "I deserve special recognition for my accomplishments.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(11, "People are often envious of me.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(12, "I like being the center of attention in most situations.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(13, "I have a very high opinion of myself.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(14, "I get frustrated when others don't notice my achievements.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(15, "I tend to seek prestige or status.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(16, "I expect people to treat me as someone important.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(17, "I enjoy being admired by others.", DarkTriadTrait.GRANDIOSITY),
        DarkTriadQuestion(18, "I often think I'm meant for greater things than most people.", DarkTriadTrait.GRANDIOSITY),

        DarkTriadQuestion(19, "I don't feel bad when others suffer, as long as it's not me.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(20, "I often act on impulse without considering consequences.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(21, "Payback and revenge feel satisfying to me.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(22, "I've said things to hurt people, and it didn't bother me much.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(23, "I get bored easily and look for excitement.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(24, "I rarely feel guilt after upsetting someone.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(25, "I sometimes provoke others just to see how they react.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(26, "I've been in trouble with authority more than once.", DarkTriadTrait.PSYCHOPATHY),
        DarkTriadQuestion(27, "I don't dwell much on how my actions affect others emotionally.", DarkTriadTrait.PSYCHOPATHY)
    )

    fun scoreByTrait(answers: Map<Int, Int>): Map<DarkTriadTrait, Double> {
        return DarkTriadTrait.entries.associateWith { trait ->
            val items = questions.filter { it.trait == trait }
            val total = items.sumOf { answers[it.id] ?: 3 }
            total.toDouble() / items.size
        }
    }

    fun bandFor(average: Double): String = when {
        average < 2.5 -> "Low"
        average <= 3.5 -> "Moderate"
        else -> "Elevated"
    }
}

enum class BigFiveTrait(val displayName: String, val description: String) {
    OPENNESS("Openness", "Your willingness to try new experiences and think outside the box."),
    CONSCIENTIOUSNESS("Conscientiousness", "How organized, dependable, and disciplined you tend to be."),
    EXTRAVERSION("Extraversion", "How much you draw energy from social interaction and the external world."),
    AGREEABLENESS("Agreeableness", "How much you value social harmony and cooperation with others."),
    NEUROTICISM("Neuroticism", "Your tendency to experience emotional instability or negative emotions.")
}

data class BigFiveQuestion(val id: Int, val text: String, val trait: BigFiveTrait, val polarity: Int)

object BigFiveTest {
    val questions: List<BigFiveQuestion> = listOf(
        // Openness
        BigFiveQuestion(1, "I enjoy hearing new ideas and exploring unconventional perspectives.", BigFiveTrait.OPENNESS, 1),
        BigFiveQuestion(2, "I prefer a fixed routine over constant change and new experiences.", BigFiveTrait.OPENNESS, -1),
        BigFiveQuestion(3, "I have a rich vocabulary and enjoy abstract thinking.", BigFiveTrait.OPENNESS, 1),
        // Conscientiousness
        BigFiveQuestion(4, "I am always prepared and keep my belongings organized.", BigFiveTrait.CONSCIENTIOUSNESS, 1),
        BigFiveQuestion(5, "I often leave my chores unfinished or procrastinate on tasks.", BigFiveTrait.CONSCIENTIOUSNESS, -1),
        BigFiveQuestion(6, "I pay attention to details and follow through on my plans.", BigFiveTrait.CONSCIENTIOUSNESS, 1),
        // Extraversion
        BigFiveQuestion(7, "I am the life of the party and enjoy being around many people.", BigFiveTrait.EXTRAVERSION, 1),
        BigFiveQuestion(8, "I prefer to keep to myself and find large crowds exhausting.", BigFiveTrait.EXTRAVERSION, -1),
        BigFiveQuestion(9, "I feel comfortable in social situations and start conversations easily.", BigFiveTrait.EXTRAVERSION, 1),
        // Agreeableness
        BigFiveQuestion(10, "I take a sincere interest in others and their well-being.", BigFiveTrait.AGREEABLENESS, 1),
        BigFiveQuestion(11, "I tend to be critical of others or skeptical of their intentions.", BigFiveTrait.AGREEABLENESS, -1),
        BigFiveQuestion(12, "I sympathize with others' feelings and try to be helpful.", BigFiveTrait.AGREEABLENESS, 1),
        // Neuroticism
        BigFiveQuestion(13, "I get upset easily and experience frequent mood swings.", BigFiveTrait.NEUROTICISM, 1),
        BigFiveQuestion(14, "I am relaxed most of the time and handle stress well.", BigFiveTrait.NEUROTICISM, -1),
        BigFiveQuestion(15, "I worry about things frequently and feel anxious under pressure.", BigFiveTrait.NEUROTICISM, 1)
    )

    fun score(answers: Map<Int, Int>): Map<BigFiveTrait, Double> {
        return BigFiveTrait.entries.associateWith { trait ->
            val items = questions.filter { it.trait == trait }
            val totalRaw = items.sumOf { q ->
                val ans = answers[q.id] ?: 3
                if (q.polarity == 1) ans else (6 - ans)
            }
            totalRaw.toDouble() / items.size
        }
    }

    fun bandFor(average: Double): String = when {
        average < 2.5 -> "Low"
        average <= 3.5 -> "Moderate"
        else -> "High"
    }
}