package com.example.rejournal.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.rejournal.data.AppTheme

private val ClassicLight = lightColorScheme(
    primary = Color(0xFF37474F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = Color(0xFF616161),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = Color(0xFF424242),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF424242),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFE0E0E0),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFC4C4C4)
)

private val ClassicDark = darkColorScheme(
    primary = Color(0xFFB0BEC5),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF3A3A3A),
    onPrimaryContainer = Color(0xFFE0E0E0),
    secondary = Color(0xFF9E9E9E),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF2E2E2E),
    onSecondaryContainer = Color(0xFFE0E0E0),
    tertiary = Color(0xFFBDBDBD),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFC4C4C4),
    surfaceContainerLowest = Color(0xFF0A0A0A),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF212121),
    surfaceContainerHigh = Color(0xFF2C2C2C),
    surfaceContainerHighest = Color(0xFF373737),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF454545)
)

private val WarmPastelLight = lightColorScheme(
    primary = Color(0xFFE8927C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC9),
    onPrimaryContainer = Color(0xFF5C2A16),
    secondary = Color(0xFFD8A48F),
    secondaryContainer = Color(0xFFFCE4D6),
    tertiary = Color(0xFFE6B89C),
    background = Color(0xFFFFF8F3),
    surface = Color(0xFFFFF3EA),
    surfaceVariant = Color(0xFFF3DFD3),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF6F0),
    surfaceContainer = Color(0xFFFCEEE4),
    surfaceContainerHigh = Color(0xFFF7E6D9),
    surfaceContainerHighest = Color(0xFFF1DDCC),
    outline = Color(0xFFB59684),
    outlineVariant = Color(0xFFE4CBB9),
    onSurface = Color(0xFF4A3B33),
    onBackground = Color(0xFF4A3B33)
)

private val WarmPastelDark = darkColorScheme(
    primary = Color(0xFFE8927C),
    onPrimary = Color(0xFF3B1608),
    primaryContainer = Color(0xFF5C2A16),
    onPrimaryContainer = Color(0xFFFFDCC9),
    secondary = Color(0xFFD8A48F),
    secondaryContainer = Color(0xFF4A362D),
    tertiary = Color(0xFFE6B89C),
    background = Color(0xFF211815),
    surface = Color(0xFF2A201C),
    surfaceVariant = Color(0xFF3D2E27),
    surfaceContainerLowest = Color(0xFF190F0C),
    surfaceContainerLow = Color(0xFF241A16),
    surfaceContainer = Color(0xFF2E221D),
    surfaceContainerHigh = Color(0xFF392C25),
    surfaceContainerHighest = Color(0xFF44362E),
    outline = Color(0xFF8E7568),
    outlineVariant = Color(0xFF4F3E34),
    onSurface = Color(0xFFEFE0D8),
    onBackground = Color(0xFFEFE0D8)
)

private val SunsetLight = lightColorScheme(
    primary = Color(0xFFE07A5F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD8C2),
    onPrimaryContainer = Color(0xFF5C1F0C),
    secondary = Color(0xFFF2CC8F),
    secondaryContainer = Color(0xFFFCEBC7),
    tertiary = Color(0xFF81B29A),
    background = Color(0xFFFFF6EE),
    surface = Color(0xFFFFEFDF),
    surfaceVariant = Color(0xFFF5DCC4),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF3E6),
    surfaceContainer = Color(0xFFFCE9D6),
    surfaceContainerHigh = Color(0xFFF6E0C8),
    surfaceContainerHighest = Color(0xFFF0D5B7),
    outline = Color(0xFFB99671),
    outlineVariant = Color(0xFFE7CBA9),
    onSurface = Color(0xFF4A3527),
    onBackground = Color(0xFF4A3527)
)

private val SunsetDark = darkColorScheme(
    primary = Color(0xFFE07A5F),
    onPrimary = Color(0xFF3D0F02),
    primaryContainer = Color(0xFF5C1F0C),
    onPrimaryContainer = Color(0xFFFFD8C2),
    secondary = Color(0xFFF2CC8F),
    secondaryContainer = Color(0xFF4A3B1E),
    tertiary = Color(0xFF81B29A),
    background = Color(0xFF231A12),
    surface = Color(0xFF2B2016),
    surfaceVariant = Color(0xFF3E2F21),
    surfaceContainerLowest = Color(0xFF1B130D),
    surfaceContainerLow = Color(0xFF261C13),
    surfaceContainer = Color(0xFF30251A),
    surfaceContainerHigh = Color(0xFF3B2E20),
    surfaceContainerHighest = Color(0xFF473727),
    outline = Color(0xFF93805F),
    outlineVariant = Color(0xFF52422E),
    onSurface = Color(0xFFF2E6D8),
    onBackground = Color(0xFFF2E6D8)
)

private val LavenderLight = lightColorScheme(
    primary = Color(0xFF9B8AC4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6DEF7),
    onPrimaryContainer = Color(0xFF3A2E5C),
    secondary = Color(0xFFC7B8E8),
    secondaryContainer = Color(0xFFF1EAFB),
    tertiary = Color(0xFFF4A9C4),
    background = Color(0xFFFAF7FE),
    surface = Color(0xFFF4EEFC),
    surfaceVariant = Color(0xFFE5DCF2),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6F1FC),
    surfaceContainer = Color(0xFFEFE7F9),
    surfaceContainerHigh = Color(0xFFE9DFF5),
    surfaceContainerHighest = Color(0xFFE1D5F0),
    outline = Color(0xFF9585B0),
    outlineVariant = Color(0xFFD6C9EA),
    onSurface = Color(0xFF433A57),
    onBackground = Color(0xFF433A57)
)

private val LavenderDark = darkColorScheme(
    primary = Color(0xFF9B8AC4),
    onPrimary = Color(0xFF241748),
    primaryContainer = Color(0xFF3A2E5C),
    onPrimaryContainer = Color(0xFFE6DEF7),
    secondary = Color(0xFFC7B8E8),
    secondaryContainer = Color(0xFF3E3560),
    tertiary = Color(0xFFF4A9C4),
    background = Color(0xFF1E1830),
    surface = Color(0xFF251E3A),
    surfaceVariant = Color(0xFF352C4F),
    surfaceContainerLowest = Color(0xFF171225),
    surfaceContainerLow = Color(0xFF201A32),
    surfaceContainer = Color(0xFF29223D),
    surfaceContainerHigh = Color(0xFF332B49),
    surfaceContainerHighest = Color(0xFF3E3555),
    outline = Color(0xFF8C7DA8),
    outlineVariant = Color(0xFF473C63),
    onSurface = Color(0xFFEAE3F7),
    onBackground = Color(0xFFEAE3F7)
)

private val MintLight = lightColorScheme(
    primary = Color(0xFF6FB3A0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3EEE5),
    onPrimaryContainer = Color(0xFF17392F),
    secondary = Color(0xFFA8D5C4),
    secondaryContainer = Color(0xFFE6F5EF),
    tertiary = Color(0xFFF6C89F),
    background = Color(0xFFF6FBF9),
    surface = Color(0xFFEDF7F3),
    surfaceVariant = Color(0xFFDCEDE6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF9F6),
    surfaceContainer = Color(0xFFE4F2EC),
    surfaceContainerHigh = Color(0xFFDAEDE4),
    surfaceContainerHighest = Color(0xFFCDE6DA),
    outline = Color(0xFF7FA598),
    outlineVariant = Color(0xFFC2DED4),
    onSurface = Color(0xFF2E4842),
    onBackground = Color(0xFF2E4842)
)

private val MintDark = darkColorScheme(
    primary = Color(0xFF6FB3A0),
    onPrimary = Color(0xFF0B2620),
    primaryContainer = Color(0xFF17392F),
    onPrimaryContainer = Color(0xFFD3EEE5),
    secondary = Color(0xFFA8D5C4),
    secondaryContainer = Color(0xFF2C4A40),
    tertiary = Color(0xFFF6C89F),
    background = Color(0xFF16221F),
    surface = Color(0xFF1D2B27),
    surfaceVariant = Color(0xFF2C3D38),
    surfaceContainerLowest = Color(0xFF0F1815),
    surfaceContainerLow = Color(0xFF19251F),
    surfaceContainer = Color(0xFF212F2A),
    surfaceContainerHigh = Color(0xFF2B3A34),
    surfaceContainerHighest = Color(0xFF35453F),
    outline = Color(0xFF6E9689),
    outlineVariant = Color(0xFF39473F),
    onSurface = Color(0xFFDCEDE6),
    onBackground = Color(0xFFDCEDE6)
)

private val BlueLight = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF42A5F5),
    secondaryContainer = Color(0xFFE3F2FD),
    tertiary = Color(0xFF00ACC1),
    background = Color(0xFFF4F9FE),
    surface = Color(0xFFEBF4FC),
    surfaceVariant = Color(0xFFD6E6F7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEDF5FD),
    surfaceContainer = Color(0xFFE1EFFB),
    surfaceContainerHigh = Color(0xFFD4E7F9),
    surfaceContainerHighest = Color(0xFFC7DFF6),
    outline = Color(0xFF5C8BB8),
    outlineVariant = Color(0xFFB0D1F0),
    onSurface = Color(0xFF0B1E33),
    onBackground = Color(0xFF0B1E33)
)

private val BlueDark = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF42A5F5),
    secondaryContainer = Color(0xFF19558C),
    tertiary = Color(0xFF80DEEA),
    background = Color(0xFF0D141C),
    surface = Color(0xFF131C26),
    surfaceVariant = Color(0xFF223142),
    surfaceContainerLowest = Color(0xFF080C12),
    surfaceContainerLow = Color(0xFF101721),
    surfaceContainer = Color(0xFF17212E),
    surfaceContainerHigh = Color(0xFF202C3D),
    surfaceContainerHighest = Color(0xFF29384C),
    outline = Color(0xFF6B87A6),
    outlineVariant = Color(0xFF33475E),
    onSurface = Color(0xFFE1EFFB),
    onBackground = Color(0xFFE1EFFB)
)

private val ArtisticGreenLight = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF4CAF50),
    secondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFF558B2F),
    background = Color(0xFFF5FBF5),
    surface = Color(0xFFEBF7EB),
    surfaceVariant = Color(0xFFD7ECD7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEDF8ED),
    surfaceContainer = Color(0xFFE1F4E1),
    surfaceContainerHigh = Color(0xFFD4ECD4),
    surfaceContainerHighest = Color(0xFFC6E4C6),
    outline = Color(0xFF5C8F5C),
    outlineVariant = Color(0xFFB0DFB0),
    onSurface = Color(0xFF0C260C),
    onBackground = Color(0xFF0C260C)
)

private val ArtisticGreenDark = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF81C784),
    secondaryContainer = Color(0xFF205024),
    tertiary = Color(0xFFAED581),
    background = Color(0xFF0E160E),
    surface = Color(0xFF142214),
    surfaceVariant = Color(0xFF243A24),
    surfaceContainerLowest = Color(0xFF080D08),
    surfaceContainerLow = Color(0xFF101C10),
    surfaceContainer = Color(0xFF182A18),
    surfaceContainerHigh = Color(0xFF223B22),
    surfaceContainerHighest = Color(0xFF2C4C2C),
    outline = Color(0xFF6B9C6B),
    outlineVariant = Color(0xFF335733),
    onSurface = Color(0xFFE1F4E1),
    onBackground = Color(0xFFE1F4E1)
)

private val EmpatheticBlueLight = lightColorScheme(
    primary = Color(0xFF4FA4B8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD0F0F7),
    onPrimaryContainer = Color(0xFF0A313A),
    secondary = Color(0xFF7FBCC9),
    secondaryContainer = Color(0xFFE6F7FA),
    tertiary = Color(0xFFF3B096),
    background = Color(0xFFF4FAFB),
    surface = Color(0xFFEBF5F7),
    surfaceVariant = Color(0xFFDCEBF0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF8FA),
    surfaceContainer = Color(0xFFE3F1F5),
    surfaceContainerHigh = Color(0xFFD9EAF0),
    surfaceContainerHighest = Color(0xFFCCE2E8),
    outline = Color(0xFF65939E),
    outlineVariant = Color(0xFFBAD6DD),
    onSurface = Color(0xFF1B353B),
    onBackground = Color(0xFF1B353B)
)

private val EmpatheticBlueDark = darkColorScheme(
    primary = Color(0xFF78D1E1),
    onPrimary = Color(0xFF0A343D),
    primaryContainer = Color(0xFF1C4D57),
    onPrimaryContainer = Color(0xFFD0F0F7),
    secondary = Color(0xFF96D5E3),
    secondaryContainer = Color(0xFF2B4E56),
    tertiary = Color(0xFFF3B096),
    background = Color(0xFF0F1A1C),
    surface = Color(0xFF162529),
    surfaceVariant = Color(0xFF273B40),
    surfaceContainerLowest = Color(0xFF0A1214),
    surfaceContainerLow = Color(0xFF122024),
    surfaceContainer = Color(0xFF1A2C31),
    surfaceContainerHigh = Color(0xFF24373D),
    surfaceContainerHighest = Color(0xFF2F434A),
    outline = Color(0xFF6C8387),
    outlineVariant = Color(0xFF354B4F),
    onSurface = Color(0xFFDCEBF0),
    onBackground = Color(0xFFDCEBF0)
)

private val SleepyPinkLight = lightColorScheme(
    primary = Color(0xFFEAA1B8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFEE3EB),
    onPrimaryContainer = Color(0xFF421221),
    secondary = Color(0xFFF3BDCD),
    secondaryContainer = Color(0xFFFFF0F4),
    tertiary = Color(0xFF90CAF9),
    background = Color(0xFFFFF5F7),
    surface = Color(0xFFFEEBF0),
    surfaceVariant = Color(0xFFFADAE3),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFEFF3),
    surfaceContainer = Color(0xFFFCE3EA),
    surfaceContainerHigh = Color(0xFFF7D9E2),
    surfaceContainerHighest = Color(0xFFF1CFDA),
    outline = Color(0xFFB58492),
    outlineVariant = Color(0xFFE4BCC7),
    onSurface = Color(0xFF4C2732),
    onBackground = Color(0xFF4C2732)
)

private val SleepyPinkDark = darkColorScheme(
    primary = Color(0xFFFAB6C9),
    onPrimary = Color(0xFF4A1525),
    primaryContainer = Color(0xFF692A3D),
    onPrimaryContainer = Color(0xFFFEE3EB),
    secondary = Color(0xFFF4CCD6),
    secondaryContainer = Color(0xFF532834),
    tertiary = Color(0xFF90CAF9),
    background = Color(0xFF241418),
    surface = Color(0xFF2F1B20),
    surfaceVariant = Color(0xFF432A31),
    surfaceContainerLowest = Color(0xFF1B0C10),
    surfaceContainerLow = Color(0xFF28161B),
    surfaceContainer = Color(0xFF331E24),
    surfaceContainerHigh = Color(0xFF3F272E),
    surfaceContainerHighest = Color(0xFF4A313A),
    outline = Color(0xFF966F7A),
    outlineVariant = Color(0xFF553942),
    onSurface = Color(0xFFFCE3EA),
    onBackground = Color(0xFFFCE3EA)
)

private val IrritatedPurpleLight = lightColorScheme(
    primary = Color(0xFF7B1FA2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3E5F5),
    onPrimaryContainer = Color(0xFF4A0072),
    secondary = Color(0xFF9C27B0),
    secondaryContainer = Color(0xFFF3E5F5),
    tertiary = Color(0xFF6A0DAD),
    background = Color(0xFFFBFAFD),
    surface = Color(0xFFFBFAFD),
    surfaceVariant = Color(0xFFF3E9F7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F3FA),
    surfaceContainer = Color(0xFFF4EDF7),
    surfaceContainerHigh = Color(0xFFEFE6F3),
    surfaceContainerHighest = Color(0xFFEAE0EF),
    outline = Color(0xFF8E709F),
    outlineVariant = Color(0xFFD3C5D8),
    onSurface = Color(0xFF1F0033),
    onBackground = Color(0xFF1F0033)
)

private val IrritatedPurpleDark = darkColorScheme(
    primary = Color(0xFFE1BEE7),
    onPrimary = Color(0xFF4A0072),
    primaryContainer = Color(0xFF7B1FA2),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondary = Color(0xFFCE93D8),
    secondaryContainer = Color(0xFF4A148C),
    tertiary = Color(0xFFEA80FC),
    background = Color(0xFF120B1A),
    surface = Color(0xFF120B1A),
    surfaceVariant = Color(0xFF2E1E3B),
    surfaceContainerLowest = Color(0xFF0C0711),
    surfaceContainerLow = Color(0xFF1A1024),
    surfaceContainer = Color(0xFF21152E),
    surfaceContainerHigh = Color(0xFF2B1B3C),
    surfaceContainerHighest = Color(0xFF36224B),
    outline = Color(0xFFA68EB8),
    outlineVariant = Color(0xFF4E385D),
    onSurface = Color(0xFFEFE5F3),
    onBackground = Color(0xFFEFE5F3)
)

private val SlowBurgundyLight = lightColorScheme(
    primary = Color(0xFF800020),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDADA),
    onPrimaryContainer = Color(0xFF400008),
    secondary = Color(0xFF7B5252),
    secondaryContainer = Color(0xFFFFDADA),
    tertiary = Color(0xFF7D5700),
    background = Color(0xFFFFF8F8),
    surface = Color(0xFFFFF8F8),
    surfaceVariant = Color(0xFFF4DDDD),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF1F1),
    surfaceContainer = Color(0xFFFCEAE9),
    surfaceContainerHigh = Color(0xFFF7E4E3),
    surfaceContainerHighest = Color(0xFFF1DEDD),
    outline = Color(0xFF857373),
    outlineVariant = Color(0xFFD8C2C2),
    onSurface = Color(0xFF2B1515),
    onBackground = Color(0xFF2B1515)
)

private val SlowBurgundyDark = darkColorScheme(
    primary = Color(0xFFFFB3B4),
    onPrimary = Color(0xFF680016),
    primaryContainer = Color(0xFF800020),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = Color(0xFFE7BDBE),
    secondaryContainer = Color(0xFF5F3B3B),
    tertiary = Color(0xFFFFB945),
    background = Color(0xFF201212),
    surface = Color(0xFF201212),
    surfaceVariant = Color(0xFF534343),
    surfaceContainerLowest = Color(0xFF1B0C0C),
    surfaceContainerLow = Color(0xFF2B1515),
    surfaceContainer = Color(0xFF321B1B),
    surfaceContainerHigh = Color(0xFF3D2525),
    surfaceContainerHighest = Color(0xFF493030),
    outline = Color(0xFFA08C8C),
    outlineVariant = Color(0xFF534343),
    onSurface = Color(0xFFF1DEDD),
    onBackground = Color(0xFFF1DEDD)
)

private fun colorSchemeFor(theme: AppTheme, darkMode: Boolean) = when (theme) {
    AppTheme.CLASSIC -> if (darkMode) ClassicDark else ClassicLight
    AppTheme.WARM_PASTEL -> if (darkMode) WarmPastelDark else WarmPastelLight
    AppTheme.SUNSET -> if (darkMode) SunsetDark else SunsetLight
    AppTheme.LAVENDER -> if (darkMode) LavenderDark else LavenderLight
    AppTheme.MINT -> if (darkMode) MintDark else MintLight
    AppTheme.BLUE -> if (darkMode) BlueDark else BlueLight
    AppTheme.ARTISTIC_GREEN -> if (darkMode) ArtisticGreenDark else ArtisticGreenLight
    AppTheme.EMPATHETIC_BLUE -> if (darkMode) EmpatheticBlueDark else EmpatheticBlueLight
    AppTheme.SLEEPY_PINK -> if (darkMode) SleepyPinkDark else SleepyPinkLight
    AppTheme.IRRITATED_PURPLE -> if (darkMode) IrritatedPurpleDark else IrritatedPurpleLight
    AppTheme.SLOW_BURGUNDY -> if (darkMode) SlowBurgundyDark else SlowBurgundyLight
}

fun previewColorFor(theme: AppTheme): Color = colorSchemeFor(theme, darkMode = false).primary

fun themePreviewColors(theme: AppTheme, darkMode: Boolean): List<Color> {
    val scheme = colorSchemeFor(theme, darkMode)
    return listOf(
        scheme.primary,
        scheme.secondary,
        scheme.tertiary,
        scheme.surfaceVariant,
        scheme.background
    )
}

fun resolvedPrimaryColor(context: android.content.Context): Color {
    val theme = com.example.rejournal.data.ThemePrefs.getTheme(context)
    val darkMode = com.example.rejournal.data.ThemePrefs.isDarkMode(context)
    return colorSchemeFor(theme, darkMode).primary
}

@Composable
fun ReJournalTheme(
    content: @Composable () -> Unit
) {
    val selectedTheme by ThemeState.current
    val darkMode by ThemeState.darkMode
    val colorScheme = colorSchemeFor(selectedTheme, darkMode)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                colorScheme.primary.luminance() > 0.5f
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}