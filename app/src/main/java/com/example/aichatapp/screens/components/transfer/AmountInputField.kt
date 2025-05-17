package com.example.aichatapp.screens.components.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aichatapp.screens.CurrencyAmountInputVisualTransformation
import java.util.Locale

@Composable
fun AmountInputField(
    message: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onAmountSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .padding(end = 100.dp, bottom = 22.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(16.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        TextField(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            value = amount,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    onAmountChange(it)
                }
            },
            visualTransformation = CurrencyAmountInputVisualTransformation(
                fixedCursorAtTheEnd = true
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val numericAmount = amount.toDoubleOrNull()?.div(100)
                    numericAmount?.let {
                        onAmountSubmitted(String.format(Locale.US, "%.2f", it))
                    }
                    keyboardController?.hide()
                }
            ),
            singleLine = true
        )
    }
}