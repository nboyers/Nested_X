package com.nobosoftware.nestedx.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nobosoftware.nestedx.android.views.UltimateTicTacToeGame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UltimateTicTacToeGame()
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
       UltimateTicTacToeGame()
    }
}
