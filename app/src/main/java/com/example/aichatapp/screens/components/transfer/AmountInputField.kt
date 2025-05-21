package com.example.aichatapp.screens.components.transfer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatapp.screens.CurrencyAmountInputVisualTransformation
import java.util.Locale

@Composable
fun AmountInputField(
    message: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onAmountSubmitted: (String) -> Unit,
    logoPainter: Painter,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 100.dp, bottom = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

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

        // Bank logo at top-left, half-outside the bubble
        Image(
            painter = logoPainter,
            contentDescription = "Bank Logo",
            modifier = Modifier
                .size(36.dp)
                .absoluteOffset(x = (-20).dp, y = (-10).dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopStart)
        )
    }
}