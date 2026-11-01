package com.movify.app.ui.compose.main

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.movify.app.ui.compose.model.MovieUiModel
import com.movify.app.ui.compose.theme.MovifyTheme

class MainScreenPreviewStates : PreviewParameterProvider<MainScreenState> {
    override val values = sequenceOf(
        MainScreenState.Loading,
        MainScreenState.Error,
        MainScreenState.Success(emptyList()),
        MainScreenState.Success(
            listOf(
                MovieUiModel(
                    id = 1,
                    title = "A very long movie title that wraps across several lines on a narrow screen",
                    releaseDate = "2026-11-01",
                    overview = "A journey through unfamiliar places with unexpected companions.",
                    posterUrl = null,
                ),
                MovieUiModel(2, null, null, null, null),
            ),
        ),
    )
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large text", showBackground = true, fontScale = 2f, widthDp = 320)
@Composable
private fun MainScreenPreview(
    @PreviewParameter(MainScreenPreviewStates::class) state: MainScreenState,
) {
    MovifyTheme {
        Surface {
            MainScreen(uiState = state, onRetry = {})
        }
    }
}
