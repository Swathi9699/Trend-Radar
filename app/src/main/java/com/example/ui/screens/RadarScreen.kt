package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.viewmodel.ViralTrendViewModel

/**
 * RadarScreen delegates to the Gemini-powered TrendRadar screen component.
 */
@Composable
fun RadarScreen(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    TrendRadar(viewModel = viewModel, modifier = modifier)
}
