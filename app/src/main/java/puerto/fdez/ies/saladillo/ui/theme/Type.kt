package puerto.fdez.ies.saladillo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import puerto.fdez.ies.saladillo.R

val RockSaltFont = FontFamily(
    Font(R.font.rocksalt)
)

val CustomTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 18.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 50.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 16.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RockSaltFont,
        fontSize = 50.sp
    )
)
