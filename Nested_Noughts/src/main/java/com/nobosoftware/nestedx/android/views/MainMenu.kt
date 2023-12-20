package com.nobosoftware.nestedx.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nobosoftware.nestedx.android.models.GameMode

@Composable
fun MainMenu(onGameModeSelected: (GameMode) -> Unit) {
    val backgroundColor = Color(0xFF3B7B78)
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "NESTED NOUGHTS",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { onGameModeSelected(GameMode.HumanVsHuman) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text("Play with Friends", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDifficultyDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
            ) {
                Text("Play with Computer", color = Color.Black)
            }

            // Show difficulty dialog when the button is clicked
            if (showDifficultyDialog) {
//                ComputerDifficultyDialog(
//                    onDifficultySelected = {
//                        // Pass the selected difficulty to the game mode selection callback
//                        onGameModeSelected(GameMode.Computer(it))
//                        showDifficultyDialog = false
//                    },
//                    onDismiss = { showDifficultyDialog = false }
//                )
            }
        }

        Text(
            text = "X",
            color = Color.Black,
            fontSize = 350.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-75).dp, y = (-180).dp)
        )

        Text(
            text = "O",
            color = Color.Black,
            fontSize = 350.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 75.dp, y = 180.dp)
        )
    }
}

@Composable
fun ComputerDifficultyDialog(
    onDifficultySelected: (GameMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Computer Difficulty") },
        buttons = {
            Column {
                Button(
                    onClick = { onDifficultySelected(GameMode.EasyAI) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Easy")
                }
                Button(
                    onClick = { onDifficultySelected(GameMode.MediumAI) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Medium")
                }
                Button(
                    onClick = { onDifficultySelected(GameMode.HardAI) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hard")
                }
                Button(
                    onClick = { onDifficultySelected(GameMode.ImpossibleAI) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Harder")
                }
            }
        }
    )
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    buttons: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                title() // Display the title composable

                Spacer(modifier = Modifier.height(16.dp))

                buttons() // Display the buttons composable
            }
        }
    }
}

