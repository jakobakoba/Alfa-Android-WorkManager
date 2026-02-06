package com.bor96dev.workmanagerdz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bor96dev.workmanagerdz.ui.theme.WorkManagerDzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManagerDzTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageRotationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageRotationScreen(
    viewModel: ImageRotationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {

}