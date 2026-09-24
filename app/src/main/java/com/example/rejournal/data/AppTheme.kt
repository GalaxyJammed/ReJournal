package com.example.rejournal.data

enum class AppThemeCategory {
    MONO,
    MULTI
}

enum class AppTheme(val displayName: String, val category: AppThemeCategory = AppThemeCategory.MONO) {
    CLASSIC("Classic", AppThemeCategory.MONO),
    SLOW_BURGUNDY("Slow Burgundy", AppThemeCategory.MONO),
    SUNSET("Sunset", AppThemeCategory.MONO),
    WARM_PASTEL("Warm Pastel", AppThemeCategory.MONO),
    ARTISTIC_GREEN("Artistic Green", AppThemeCategory.MONO),
    MINT("Minty Fresh", AppThemeCategory.MONO),
    EMPATHETIC_BLUE("Empathetic Blue", AppThemeCategory.MONO),
    BLUE("Ocean Breeze", AppThemeCategory.MONO),
    LAVENDER("Lavender Dream", AppThemeCategory.MONO),
    IRRITATED_PURPLE("Irritated Purple", AppThemeCategory.MONO),
    SLEEPY_PINK("Sleepy Pink", AppThemeCategory.MONO),

    PRIMARY_POP("Primary Pop", AppThemeCategory.MULTI),
    ELECTRIC_OAT("Electric Oat", AppThemeCategory.MULTI),
    CITRUS_PLUM("Citrus Plum", AppThemeCategory.MULTI),
    HARVEST_PUMPKIN("Harvest Pumpkin", AppThemeCategory.MULTI),
    BERRY_MIDNIGHT("Berry Midnight", AppThemeCategory.MULTI),
    SUPER_SONIC("Super Sonic", AppThemeCategory.MULTI),
    TROPICAL_FREEZE("Tropical Freeze", AppThemeCategory.MULTI),
    CARIBBEAN_COCOA("Caribbean Cocoa", AppThemeCategory.MULTI),
    MARTINI_VELVET("Martini Velvet", AppThemeCategory.MULTI)
}