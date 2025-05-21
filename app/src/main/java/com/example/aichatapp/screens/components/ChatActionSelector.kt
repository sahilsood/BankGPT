package com.example.aichatapp.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatapp.R

@Composable
fun ChatActionSelector(
    message: String,
    defaultSelected: String = "transfer",
    onConfirm: (String) -> Unit,
    logoPainter: Painter
) {
    var selectedAction by remember { mutableStateOf(defaultSelected) }
    var confirmed by remember { mutableStateOf(false) }

    if (!confirmed) {
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
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val options = listOf(
                    Triple("transfer", "Transfer", R.drawable.ic_transfer),
                    Triple("bill_pay", "Bill Pay", R.drawable.ic_bill_pay),
                    Triple("zelle", "Zelle", R.drawable.ic_zelle),
                    Triple("wire_transfer", "Wire Transfer", R.drawable.ic_wire_transfer)
                )

                options.forEach { (value, label, iconRes) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedAction == value)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                            .clickable { selectedAction = value }
                            .padding(12.dp)
                    ) {
                        RadioButton(
                            selected = selectedAction == value,
                            onClick = { selectedAction = value },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "$label icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )

                        Text(
                            text = label,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Button(
                    onClick = {
                        confirmed = true
                        onConfirm(selectedAction)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Confirm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
}