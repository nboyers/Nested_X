import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.models.GameMode

@Composable
fun MainMenu(onGameModeSelected: (GameMode) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val maxButtonWidth = maxWidth.times(0.8f)

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tic Tac Toe",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            val gameModes = listOf(
                "Human vs Human" to GameMode.HumanVsHuman,
                "Easy" to GameMode.EasyAI,
                "Medium" to GameMode.MediumAI,
                "Hard" to GameMode.HardAI,
                "Impossible" to GameMode.ImpossibleAI
            )

            gameModes.forEach { (label, mode) ->
                GameModeButton(
                    label = label,
                    onClick = { onGameModeSelected(mode) },
                    maxButtonWidth = maxButtonWidth
                )
            }
        }
    }
}

@Composable
fun GameModeButton(label: String, onClick: () -> Unit, maxButtonWidth: Dp) {
    var isHovered by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isHovered, label = "ButtonHover")

    val backgroundColor by transition.animateColor(label = "BackgroundColor") { hovered ->
        if (hovered) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.tertiary
    }

    val contentColor by transition.animateColor(label = "ContentColor") { hovered ->
        if (hovered) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onTertiary
    }

    val elevation by transition.animateDp(label = "Elevation") { hovered ->
        if (hovered) 10.dp else 5.dp
    }

    Button(
        onClick, Modifier
            .widthIn(max = maxButtonWidth)
            .padding(vertical = 8.dp),
//            .pointerMoveFilter(
//                onEnter = { isHovered = true; false },
//                onExit = { isHovered = false; false }
//            ),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = contentColor
            )
        )
    }
}
