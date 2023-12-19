package tabletop.common.dnd5e.character

val experiencePointsByLevel = mapOf(
    1 to 0L,
    2 to 300L,
    3 to 900L,
    4 to 2700L,
    5 to 6500L,
    6 to 14000L,
    7 to 23000L,
    8 to 34000L,
    9 to 48000L,
    10 to 64000L,
    11 to 85000L,
    12 to 100000L,
    13 to 120000L,
    14 to 140000L,
    15 to 165000L,
    16 to 195000L,
    17 to 225000L,
    18 to 265000L,
    19 to 305000L,
    20 to 355000L,
)

fun nextLevelExperience(experience: Long): Long {
    return experiencePointsByLevel.entries
        .sortedBy { it.value }
        .firstOrNull { it.value > experience }
        ?.value
        ?: 355000L
}

fun levelFromExperience(experience: Long): Int {
    return experiencePointsByLevel.entries
        .sortedBy { it.value }
        .firstOrNull { it.value > experience }
        ?.key
        ?: 20
}