package xyz.sevive.pdfimex.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.sevive.pdfimex.core.ExtractStrategy
import xyz.sevive.pdfimex.core.SimpleExtractStrategy
import xyz.sevive.pdfimex.core.SlicedExtractStrategy

@Composable
fun ExtractStrategySelector(
    currentStrategy: ExtractStrategy,
    onStrategyChange: (ExtractStrategy) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(modifier) {
        for (strategy in listOf(SimpleExtractStrategy, SlicedExtractStrategy)) {
            Row(
                Modifier.clickable(enabled = enabled) { onStrategyChange(strategy) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = currentStrategy.getName() == strategy.getName(),
                    onClick = { },
                    enabled = enabled,
                )

                Text(strategy.getName())
            }
        }
    }
}
