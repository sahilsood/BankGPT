package com.example.aichatapp.screens.components.zelle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aichatapp.R

@Preview
@Composable
fun ZelleContactList(
    message: String = "Select an option:",
    logoPainter: Painter = painterResource(id = R.drawable.ic_launcher_foreground),
    onOptionSelected: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 100.dp, bottom = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ZelleContactItem(
                    "B",
                    "Bhavya HU",
                    "732-875-8536",
                    painterResource(id = R.drawable.ic_zelle)
                ) {
                    onOptionSelected(it)
                }
                ZelleContactItem(
                    "D",
                    "Devraj Uncc",
                    "848-248-1406",
                    painterResource(id = R.drawable.ic_zelle)
                ) {
                    onOptionSelected(it)
                }
            }
        }

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

@Composable
fun ZelleContactItem(
    initial: String = "B",
    name: String = "Bhavya HU",
    phone: String = "732-875-8536",
    zelleIcon: Painter = painterResource(id = R.drawable.ic_zelle),
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick(name) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            // Profile circle
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Zelle icon overlay
            Image(
                painter = zelleIcon,
                contentDescription = "Zelle",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = phone,
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 12.sp
        )
    }
}