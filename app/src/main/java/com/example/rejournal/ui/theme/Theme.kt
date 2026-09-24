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
    primary = Color(0xFFF06292),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFCE4EC),
    onPrimaryContainer = Color(0xFF3F001D),
    secondary = Color(0xFFF48FB1),
    secondaryContainer = Color(0xFFFFF0F5),
    tertiary = Color(0xFF90CAF9),
    background = Color(0xFFFFFBFC),
    surface = Color(0xFFFFFBFC),
    surfaceVariant = Color(0xFFFFF0F3),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF5F8),
    surfaceContainer = Color(0xFFFFF0F4),
    surfaceContainerHigh = Color(0xFFFBE4EA),
    surfaceContainerHighest = Color(0xFFF6D9E1),
    outline = Color(0xFF8E7079),
    outlineVariant = Color(0xFFD8C2C8),
    onSurface = Color(0xFF2B151B),
    onBackground = Color(0xFF2B151B)
)

private val SleepyPinkDark = darkColorScheme(
    primary = Color(0xFFF48FB1),
    onPrimary = Color(0xFF4E0027),
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = Color(0xFFFCE4EC),
    secondary = Color(0xFFF06292),
    secondaryContainer = Color(0xFF5F3B48),
    tertiary = Color(0xFF90CAF9),
    background = Color(0xFF2D1B22),
    surface = Color(0xFF2D1B22),
    surfaceVariant = Color(0xFF534348),
    surfaceContainerLowest = Color(0xFF201217),
    surfaceContainerLow = Color(0xFF38252C),
    surfaceContainer = Color(0xFF3D272E),
    surfaceContainerHigh = Color(0xFF4A313A),
    surfaceContainerHighest = Color(0xFF573C46),
    outline = Color(0xFFA08C92),
    outlineVariant = Color(0xFF534348),
    onSurface = Color(0xFFFCE4E9),
    onBackground = Color(0xFFFCE4E9)
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

private val PrimaryPopLight = lightColorScheme(
    primary = Color(0xFF003865),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E3F5),
    onPrimaryContainer = Color(0xFF001D38),
    secondary = Color(0xFFEE2737),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002),
    tertiary = Color(0xFFD4AC0D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFF4C2),
    onTertiaryContainer = Color(0xFF3B2D00),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFF1F5F9),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF42474E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
    outline = Color(0xFF72777F),
    outlineVariant = Color(0xFFC2C7CE)
)

private val PrimaryPopDark = darkColorScheme(
    primary = Color(0xFF8BCEFF),
    onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF004A79),
    onPrimaryContainer = Color(0xFFD4E3F5),
    secondary = Color(0xFFFFB4AB),
    onSecondary = Color(0xFF690005),
    secondaryContainer = Color(0xFF93000A),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFF4D03F),
    onTertiary = Color(0xFF3B2D00),
    tertiaryContainer = Color(0xFF554400),
    onTertiaryContainer = Color(0xFFFFF4C2),
    background = Color(0xFF0F141A),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF171E26),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF26323D),
    onSurfaceVariant = Color(0xFFC2C7CE),
    surfaceContainerLowest = Color(0xFF0A0E13),
    surfaceContainerLow = Color(0xFF121820),
    surfaceContainer = Color(0xFF19222B),
    surfaceContainerHigh = Color(0xFF222C38),
    surfaceContainerHighest = Color(0xFF2B3745),
    outline = Color(0xFF8C9199),
    outlineVariant = Color(0xFF42474E)
)


private val ElectricOatLight = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDBE6FF),
    onPrimaryContainer = Color(0xFF001552),
    secondary = Color(0xFFD92B88),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD8EC),
    onSecondaryContainer = Color(0xFF3B0021),
    tertiary = Color(0xFF8C7A6B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF2E6D8),
    onTertiaryContainer = Color(0xFF2E241B),
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF201A17),
    surface = Color(0xFFF4EFEA),
    onSurface = Color(0xFF201A17),
    surfaceVariant = Color(0xFFE8DFC3),
    onSurfaceVariant = Color(0xFF4E453E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF5F0),
    surfaceContainer = Color(0xFFF2ECE6),
    surfaceContainerHigh = Color(0xFFEAE2DC),
    surfaceContainerHighest = Color(0xFFE2D9D2),
    outline = Color(0xFF7F756D),
    outlineVariant = Color(0xFFD0C4BB)
)

private val ElectricOatDark = darkColorScheme(
    primary = Color(0xFFB3C5FF),
    onPrimary = Color(0xFF002584),
    primaryContainer = Color(0xFF0038B3),
    onPrimaryContainer = Color(0xFFDBE6FF),
    secondary = Color(0xFFFFB0D9),
    onSecondary = Color(0xFF5E0038),
    secondaryContainer = Color(0xFF850052),
    onSecondaryContainer = Color(0xFFFFD8EC),
    tertiary = Color(0xFFD6C5B3),
    onTertiary = Color(0xFF392E23),
    tertiaryContainer = Color(0xFF524438),
    onTertiaryContainer = Color(0xFFF2E6D8),
    background = Color(0xFF1A1614),
    onBackground = Color(0xFFECE0D8),
    surface = Color(0xFF221C19),
    onSurface = Color(0xFFECE0D8),
    surfaceVariant = Color(0xFF382F2A),
    onSurfaceVariant = Color(0xFFD0C4BB),
    surfaceContainerLowest = Color(0xFF120E0D),
    surfaceContainerLow = Color(0xFF1D1815),
    surfaceContainer = Color(0xFF251F1B),
    surfaceContainerHigh = Color(0xFF302924),
    surfaceContainerHighest = Color(0xFF3B332D),
    outline = Color(0xFF998E85),
    outlineVariant = Color(0xFF4E453E)
)

private val CitrusPlumLight = lightColorScheme(
    primary = Color(0xFF7A1F5C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD7EC),
    onPrimaryContainer = Color(0xFF350026),
    secondary = Color(0xFFE65100),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDCCB),
    onSecondaryContainer = Color(0xFF350B00),
    tertiary = Color(0xFF8B7355),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5E6D3),
    onTertiaryContainer = Color(0xFF2C2010),
    background = Color(0xFFFAF6F2),
    onBackground = Color(0xFF231A20),
    surface = Color(0xFFF5EEF2),
    onSurface = Color(0xFF231A20),
    surfaceVariant = Color(0xFFEADBCE),
    onSurfaceVariant = Color(0xFF51434B),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF3F6),
    surfaceContainer = Color(0xFFF3EBEF),
    surfaceContainerHigh = Color(0xFFEDE2E7),
    surfaceContainerHighest = Color(0xFFE5D9E0),
    outline = Color(0xFF83737C),
    outlineVariant = Color(0xFFD5C2CC)
)

private val CitrusPlumDark = darkColorScheme(
    primary = Color(0xFFF6B0DF),
    onPrimary = Color(0xFF490036),
    primaryContainer = Color(0xFF63024B),
    onPrimaryContainer = Color(0xFFFFD7EC),
    secondary = Color(0xFFFFB59B),
    onSecondary = Color(0xFF5B1A00),
    secondaryContainer = Color(0xFF802900),
    onSecondaryContainer = Color(0xFFFFDCCB),
    tertiary = Color(0xFFE3D5CA),
    onTertiary = Color(0xFF3B2E1E),
    tertiaryContainer = Color(0xFF544433),
    onTertiaryContainer = Color(0xFFF5E6D3),
    background = Color(0xFF1C131A),
    onBackground = Color(0xFFF1E0E9),
    surface = Color(0xFF241A22),
    onSurface = Color(0xFFF1E0E9),
    surfaceVariant = Color(0xFF3B2C35),
    onSurfaceVariant = Color(0xFFD5C2CC),
    surfaceContainerLowest = Color(0xFF140D13),
    surfaceContainerLow = Color(0xFF1F161D),
    surfaceContainer = Color(0xFF271C24),
    surfaceContainerHigh = Color(0xFF32262E),
    surfaceContainerHighest = Color(0xFF3D3039),
    outline = Color(0xFF9D8C96),
    outlineVariant = Color(0xFF51434B)
)

private val HarvestPumpkinLight = lightColorScheme(
    primary = Color(0xFFB24343),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD7),
    onPrimaryContainer = Color(0xFF410005),
    secondary = Color(0xFFD35400),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDCCB),
    onSecondaryContainer = Color(0xFF350B00),
    tertiary = Color(0xFF887361),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF8E7D6),
    onTertiaryContainer = Color(0xFF2A1C10),
    background = Color(0xFFFAF6F2),
    onBackground = Color(0xFF221A18),
    surface = Color(0xFFF6EEE8),
    onSurface = Color(0xFF221A18),
    surfaceVariant = Color(0xFFEADBCF),
    onSurfaceVariant = Color(0xFF52433F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF3ED),
    surfaceContainer = Color(0xFFF3EBE4),
    surfaceContainerHigh = Color(0xFFECE2DB),
    surfaceContainerHighest = Color(0xFFE4D9D1),
    outline = Color(0xFF85736E),
    outlineVariant = Color(0xFFD8C2BB)
)

private val HarvestPumpkinDark = darkColorScheme(
    primary = Color(0xFFFFB3AC),
    onPrimary = Color(0xFF680010),
    primaryContainer = Color(0xFF8F2A2C),
    onPrimaryContainer = Color(0xFFFFDAD7),
    secondary = Color(0xFFFFB59B),
    onSecondary = Color(0xFF5B1A00),
    secondaryContainer = Color(0xFF812A00),
    onSecondaryContainer = Color(0xFFFFDCCB),
    tertiary = Color(0xFFE5D5C5),
    onTertiary = Color(0xFF392D21),
    tertiaryContainer = Color(0xFF524436),
    onTertiaryContainer = Color(0xFFF8E7D6),
    background = Color(0xFF1B1210),
    onBackground = Color(0xFFF1E0DA),
    surface = Color(0xFF231A17),
    onSurface = Color(0xFFF1E0DA),
    surfaceVariant = Color(0xFF3B2D29),
    onSurfaceVariant = Color(0xFFD8C2BB),
    surfaceContainerLowest = Color(0xFF130C0A),
    surfaceContainerLow = Color(0xFF1E1512),
    surfaceContainer = Color(0xFF271D1A),
    surfaceContainerHigh = Color(0xFF322724),
    surfaceContainerHighest = Color(0xFF3D322E),
    outline = Color(0xFFA08C87),
    outlineVariant = Color(0xFF52433F)
)

private val BerryMidnightLight = lightColorScheme(
    primary = Color(0xFF3F4F8E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF001345),
    secondary = Color(0xFFD32F45),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD9),
    onSecondaryContainer = Color(0xFF40000B),
    tertiary = Color(0xFF1B2838),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4FA),
    onTertiaryContainer = Color(0xFF001D33),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF181C22),
    surface = Color(0xFFF0F4F9),
    onSurface = Color(0xFF181C22),
    surfaceVariant = Color(0xFFE0E4F0),
    onSurfaceVariant = Color(0xFF434654),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF6F8FD),
    surfaceContainer = Color(0xFFEEF2F8),
    surfaceContainerHigh = Color(0xFFE5EBF2),
    surfaceContainerHighest = Color(0xFFDCE2EB),
    outline = Color(0xFF747685),
    outlineVariant = Color(0xFFC4C5D6)
)

private val BerryMidnightDark = darkColorScheme(
    primary = Color(0xFFB8C4FF),
    onPrimary = Color(0xFF08215D),
    primaryContainer = Color(0xFF263775),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFFFB3B5),
    onSecondary = Color(0xFF680016),
    secondaryContainer = Color(0xFF920023),
    onSecondaryContainer = Color(0xFFFFDAD9),
    tertiary = Color(0xFF8FB9E4),
    onTertiary = Color(0xFF003353),
    tertiaryContainer = Color(0xFF10283C),
    onTertiaryContainer = Color(0xFFD3E4FA),
    background = Color(0xFF0F141A),
    onBackground = Color(0xFFE0E6F0),
    surface = Color(0xFF161C24),
    onSurface = Color(0xFFE0E6F0),
    surfaceVariant = Color(0xFF2A313C),
    onSurfaceVariant = Color(0xFFC4C5D6),
    surfaceContainerLowest = Color(0xFF0A0E13),
    surfaceContainerLow = Color(0xFF121820),
    surfaceContainer = Color(0xFF19202A),
    surfaceContainerHigh = Color(0xFF232B36),
    surfaceContainerHighest = Color(0xFF2E3642),
    outline = Color(0xFF8E90A0),
    outlineVariant = Color(0xFF434654)
)

private val SuperSonicLight = lightColorScheme(
    primary = Color(0xFF1D4088),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = Color(0xFF001945),
    secondary = Color(0xFF00897B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF927C00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE266),
    onTertiaryContainer = Color(0xFF2B2300),
    background = Color(0xFFF4FAF9),
    onBackground = Color(0xFF151D21),
    surface = Color(0xFFEAF5F4),
    onSurface = Color(0xFF151D21),
    surfaceVariant = Color(0xFFD6E6E3),
    onSurfaceVariant = Color(0xFF3F4947),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF8F7),
    surfaceContainer = Color(0xFFE6F2F0),
    surfaceContainerHigh = Color(0xFFDCECE9),
    surfaceContainerHighest = Color(0xFFD0E4E1),
    outline = Color(0xFF6F7977),
    outlineVariant = Color(0xFFBEC9C6)
)

private val SuperSonicDark = darkColorScheme(
    primary = Color(0xFFB0C6FF),
    onPrimary = Color(0xFF002B73),
    primaryContainer = Color(0xFF003F9E),
    onPrimaryContainer = Color(0xFFD9E2FF),
    secondary = Color(0xFF4ECDC4),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFE6CC33),
    onTertiary = Color(0xFF382F00),
    tertiaryContainer = Color(0xFF524500),
    onTertiaryContainer = Color(0xFFFFE266),
    background = Color(0xFF0D1618),
    onBackground = Color(0xFFDEE9E7),
    surface = Color(0xFF132023),
    onSurface = Color(0xFFDEE9E7),
    surfaceVariant = Color(0xFF273537),
    onSurfaceVariant = Color(0xFFBEC9C6),
    surfaceContainerLowest = Color(0xFF090F11),
    surfaceContainerLow = Color(0xFF101C1E),
    surfaceContainer = Color(0xFF172528),
    surfaceContainerHigh = Color(0xFF203033),
    surfaceContainerHighest = Color(0xFF2A3B3F),
    outline = Color(0xFF899391),
    outlineVariant = Color(0xFF3F4947)
)

private val TropicalFreezeLight = lightColorScheme(
    primary = Color(0xFF5A832A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCF8B3),
    onPrimaryContainer = Color(0xFF162B00),
    secondary = Color(0xFF006874),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFA2EBF3),
    onSecondaryContainer = Color(0xFF001F24),
    tertiary = Color(0xFFD32F45),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDAD9),
    onTertiaryContainer = Color(0xFF40000B),
    background = Color(0xFFF7FAF4),
    onBackground = Color(0xFF1A1F16),
    surface = Color(0xFFEEF5E8),
    onSurface = Color(0xFF1A1F16),
    surfaceVariant = Color(0xFFDDE7D3),
    onSurfaceVariant = Color(0xFF434A3C),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F8EC),
    surfaceContainer = Color(0xFFE8F1E1),
    surfaceContainerHigh = Color(0xFFDFEAD6),
    surfaceContainerHighest = Color(0xFFD4E2CA),
    outline = Color(0xFF737A6B),
    outlineVariant = Color(0xFFC2CBB8)
)

private val TropicalFreezeDark = darkColorScheme(
    primary = Color(0xFF97BC62),
    onPrimary = Color(0xFF233600),
    primaryContainer = Color(0xFF395010),
    onPrimaryContainer = Color(0xFFDCF8B3),
    secondary = Color(0xFF80D5E3),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = Color(0xFFA2EBF3),
    tertiary = Color(0xFFFFB3B5),
    onTertiary = Color(0xFF680016),
    tertiaryContainer = Color(0xFF920023),
    onTertiaryContainer = Color(0xFFFFDAD9),
    background = Color(0xFF11170E),
    onBackground = Color(0xFFE2E8DC),
    surface = Color(0xFF182114),
    onSurface = Color(0xFFE2E8DC),
    surfaceVariant = Color(0xFF2D3727),
    onSurfaceVariant = Color(0xFFC2CBB8),
    surfaceContainerLowest = Color(0xFF0A1008),
    surfaceContainerLow = Color(0xFF131D10),
    surfaceContainer = Color(0xFF1B2617),
    surfaceContainerHigh = Color(0xFF24311F),
    surfaceContainerHighest = Color(0xFF2E3C29),
    outline = Color(0xFF8C9484),
    outlineVariant = Color(0xFF434A3C)
)

private val CaribbeanCocoaLight = lightColorScheme(
    primary = Color(0xFF00A896),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB4F2E8),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFF5A3A36),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD5),
    onSecondaryContainer = Color(0xFF2B1210),
    tertiary = Color(0xFF7A6800),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFF1A8),
    onTertiaryContainer = Color(0xFF262000),
    background = Color(0xFFF4FAF9),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFEBF5F3),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E2),
    onSurfaceVariant = Color(0xFF3F4947),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0F8F6),
    surfaceContainer = Color(0xFFE6F2EE),
    surfaceContainerHigh = Color(0xFFDCECE7),
    surfaceContainerHighest = Color(0xFFD0E4DE),
    outline = Color(0xFF6F7977),
    outlineVariant = Color(0xFFBEC9C6)
)

private val CaribbeanCocoaDark = darkColorScheme(
    primary = Color(0xFF56E2CF),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005047),
    onPrimaryContainer = Color(0xFFB4F2E8),
    secondary = Color(0xFFE7C1BD),
    onSecondary = Color(0xFF442421),
    secondaryContainer = Color(0xFF5D3A36),
    onSecondaryContainer = Color(0xFFFFDAD5),
    tertiary = Color(0xFFF3E5AB),
    onTertiary = Color(0xFF3B3100),
    tertiaryContainer = Color(0xFF564800),
    onTertiaryContainer = Color(0xFFFFF1A8),
    background = Color(0xFF0E1514),
    onBackground = Color(0xFFE0E9E7),
    surface = Color(0xFF14201E),
    onSurface = Color(0xFFE0E9E7),
    surfaceVariant = Color(0xFF283533),
    onSurfaceVariant = Color(0xFFBEC9C6),
    surfaceContainerLowest = Color(0xFF080F0E),
    surfaceContainerLow = Color(0xFF101B1A),
    surfaceContainer = Color(0xFF172523),
    surfaceContainerHigh = Color(0xFF21302E),
    surfaceContainerHighest = Color(0xFF2B3B39),
    outline = Color(0xFF899391),
    outlineVariant = Color(0xFF3F4947)
)

private val MartiniVelvetLight = lightColorScheme(
    primary = Color(0xFF6B7239),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF3F8B3),
    onPrimaryContainer = Color(0xFF1F2400),
    secondary = Color(0xFFB84061),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF3E0017),
    tertiary = Color(0xFF3B2B28),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF8DDD8),
    onTertiaryContainer = Color(0xFF291714),
    background = Color(0xFFFAF9F5),
    onBackground = Color(0xFF1D1C18),
    surface = Color(0xFFF3F1EC),
    onSurface = Color(0xFF1D1C18),
    surfaceVariant = Color(0xFFE5E3D8),
    onSurfaceVariant = Color(0xFF47473E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F5EF),
    surfaceContainer = Color(0xFFEFECE6),
    surfaceContainerHigh = Color(0xFFE7E4DE),
    surfaceContainerHighest = Color(0xFFDFDCD5),
    outline = Color(0xFF78776D),
    outlineVariant = Color(0xFFC9C7BC)
)

private val MartiniVelvetDark = darkColorScheme(
    primary = Color(0xFFD7DB98),
    onPrimary = Color(0xFF353909),
    primaryContainer = Color(0xFF4F5321),
    onPrimaryContainer = Color(0xFFF3F8B3),
    secondary = Color(0xFFFFB2C5),
    onSecondary = Color(0xFF65082E),
    secondaryContainer = Color(0xFF852345),
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFFE2C1BA),
    onTertiary = Color(0xFF3F2B28),
    tertiaryContainer = Color(0xFF58413D),
    onTertiaryContainer = Color(0xFFF8DDD8),
    background = Color(0xFF151310),
    onBackground = Color(0xFFE7E2DB),
    surface = Color(0xFF1D1B17),
    onSurface = Color(0xFFE7E2DB),
    surfaceVariant = Color(0xFF32302A),
    onSurfaceVariant = Color(0xFFC9C7BC),
    surfaceContainerLowest = Color(0xFF0E0D0A),
    surfaceContainerLow = Color(0xFF171512),
    surfaceContainer = Color(0xFF211E1A),
    surfaceContainerHigh = Color(0xFF2C2924),
    surfaceContainerHighest = Color(0xFF37342E),
    outline = Color(0xFF929186),
    outlineVariant = Color(0xFF47473E)
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

    AppTheme.PRIMARY_POP -> if (darkMode) PrimaryPopDark else PrimaryPopLight
    AppTheme.ELECTRIC_OAT -> if (darkMode) ElectricOatDark else ElectricOatLight
    AppTheme.CITRUS_PLUM -> if (darkMode) CitrusPlumDark else CitrusPlumLight
    AppTheme.HARVEST_PUMPKIN -> if (darkMode) HarvestPumpkinDark else HarvestPumpkinLight
    AppTheme.BERRY_MIDNIGHT -> if (darkMode) BerryMidnightDark else BerryMidnightLight
    AppTheme.SUPER_SONIC -> if (darkMode) SuperSonicDark else SuperSonicLight
    AppTheme.TROPICAL_FREEZE -> if (darkMode) TropicalFreezeDark else TropicalFreezeLight
    AppTheme.CARIBBEAN_COCOA -> if (darkMode) CaribbeanCocoaDark else CaribbeanCocoaLight
    AppTheme.MARTINI_VELVET -> if (darkMode) MartiniVelvetDark else MartiniVelvetLight
}

fun previewColorFor(theme: AppTheme): Color = colorSchemeFor(theme, darkMode = false).primary

fun themePreviewColors(theme: AppTheme, darkMode: Boolean): List<Color> {
    val scheme = colorSchemeFor(theme, darkMode)
    val colors = listOf(
        scheme.primary,
        scheme.primaryContainer,
        scheme.secondary,
        scheme.secondaryContainer,
        scheme.tertiary
    )
    return colors.sortedByDescending { it.luminance() }
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