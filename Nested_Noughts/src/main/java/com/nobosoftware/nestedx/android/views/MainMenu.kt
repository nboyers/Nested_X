package com.nobosoftware.nestedx.android.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.models.GameMode

@Composable
fun MainMenu(onGameModeSelected: (GameMode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        Button(
            onClick = { onGameModeSelected(GameMode.HumanVsHuman) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Human vs Human")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onGameModeSelected(GameMode.EasyAI) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Easy")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onGameModeSelected(GameMode.MediumAI) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Medium")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onGameModeSelected(GameMode.HardAI) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Hard")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onGameModeSelected(GameMode.ImpossibleAI) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Impossible")
        }
    }
}

